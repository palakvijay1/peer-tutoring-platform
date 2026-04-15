package com.peertutoring.service.impl;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.DuplicateResourceException;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import com.peertutoring.pattern.factory.AnswerFactory;
import com.peertutoring.pattern.observer.QuestionEvent;
import com.peertutoring.pattern.observer.QuestionEventPublisher;
import com.peertutoring.repository.AnswerRepository;
import com.peertutoring.repository.QuestionRepository;
import com.peertutoring.repository.UserRepository;
import com.peertutoring.service.AnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ══════════════════════════════════════════════════════════════
 * CREATIONAL — Singleton Pattern:
 * @Service makes Spring create exactly ONE instance of this class.
 * That single instance is reused for every request.
 * This is Spring's implementation of the Singleton pattern.
 *
 * SOLID — Single Responsibility Principle (SRP):
 * This class only handles answer-related business logic.
 * Auth logic is in AuthServiceImpl.
 * Question logic is in QuestionServiceImpl.
 *
 * SOLID — Open/Closed Principle (OCP):
 * New answer operations (e.g. upvoting) can be added as new
 * methods without modifying existing ones.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 * Depends on AnswerRepository, QuestionRepository, UserRepository
 * (all abstractions/interfaces), not concrete implementations.
 *
 * GRASP — Information Expert:
 * This service has access to all repositories it needs, making it
 * the right place to enforce all answer-related business rules.
 *
 * GRASP — Low Coupling:
 * Reuses existing exceptions (ResourceNotFoundException,
 * DuplicateResourceException) and Observer infrastructure.
 * No unnecessary new dependencies introduced.
 * ══════════════════════════════════════════════════════════════
 */
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerFactory answerFactory;           // Factory Pattern
    private final QuestionEventPublisher eventPublisher; // Observer Pattern

    // ── Story 1: Submit Answer ────────────────────────────────────────────────

    @Override
    @Transactional
    public AnswerResponse submitAnswer(Long questionId, SubmitAnswerRequest request, String email) {

        // STEP 1 — Get the student submitting the answer
        User student = getUser(email);

        // STEP 2 — Only STUDENT role can submit answers
        if (student.getRole() != User.Role.STUDENT) {
            throw new ResourceNotFoundException("Only students can submit answers.");
        }

        // STEP 3 — Get the question
        Question question = getQuestion(questionId);

        // STEP 4 — Student cannot answer their own question
        // GRASP Information Expert: service knows both the question poster
        // and the current user, so it enforces this rule here
        if (question.getPostedBy().getEmail().equalsIgnoreCase(email)) {
            throw new DuplicateResourceException("You cannot answer your own question.");
        }

        // STEP 5 — Student cannot answer the same question twice
        if (answerRepository.existsByQuestionAndSubmittedBy(question, student)) {
            throw new DuplicateResourceException("You have already answered this question.");
        }

        // STEP 6 — Create the Answer using Factory Pattern
        // AnswerFactory centralises Answer object creation.
        // If we need to add new fields to Answer creation later,
        // we change only AnswerFactory — nothing else.
        Answer answer = answerFactory.createStudentAnswer(request.getText(), question, student);

        // STEP 7 — Save the answer
        Answer saved = answerRepository.save(answer);

        // STEP 8 — Update question status to ANSWERED if it was PENDING
        // Once any answer is submitted, question moves to ANSWERED internally
        // (UI still shows "Pending" until faculty verifies)
        if (question.getStatus() == Question.QuestionStatus.PENDING) {
            String previousStatus = question.getStatus().name();
            question.setStatus(Question.QuestionStatus.ANSWERED);
            questionRepository.save(question);

            // Observer Pattern: fire status change event
            // AuditListener and NotificationListener react automatically
            eventPublisher.publishStatusChanged(
                    QuestionEvent.statusChanged(question, previousStatus));
        }

        return AnswerResponse.from(saved);
    }

    // ── Story 2: View Answers ─────────────────────────────────────────────────

    @Override
    public List<AnswerResponse> getAnswersForQuestion(Long questionId, String email) {

        User requester = getUser(email);
        Question question = getQuestion(questionId);

        List<Answer> answers = answerRepository.findByQuestionOrderByCreatedAtDesc(question);

        // FACULTY sees ALL answers (to decide which to verify)
        // STUDENTS see only the VERIFIED answer (if any)
        if (requester.getRole() == User.Role.FACULTY) {
            return answers.stream()
                    .map(AnswerResponse::from)
                    .collect(Collectors.toList());
        } else {
            // Students only see verified answers
            // If no verified answer exists yet, empty list is returned
            return answers.stream()
                    .filter(Answer::isVerified)
                    .map(AnswerResponse::from)
                    .collect(Collectors.toList());
        }
    }

    // ── Story 3: Verify Answer (Faculty only) ─────────────────────────────────

    @Override
    @Transactional
    public AnswerResponse verifyAnswer(Long questionId, VerifyAnswerRequest request, String email) {

        // STEP 1 — Check the requester is FACULTY
        // GRASP Information Expert: service enforces role-based rules
        User faculty = getUser(email);
        if (faculty.getRole() != User.Role.FACULTY) {
            throw new ResourceNotFoundException("Only faculty can verify answers.");
        }

        // STEP 2 — Get the question
        Question question = getQuestion(questionId);

        // STEP 3 — Get the answer to verify
        Answer answer = answerRepository.findById(request.getAnswerId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Answer not found with id: " + request.getAnswerId()));

        // STEP 4 — Make sure the answer actually belongs to this question
        if (!answer.getQuestion().getId().equals(questionId)) {
            throw new ResourceNotFoundException("Answer does not belong to this question.");
        }

        // STEP 5 — If another answer was previously verified, unverify it
        // Only ONE verified answer allowed per question
        answerRepository.findByQuestionAndVerifiedTrue(question)
                .ifPresent(existingVerified -> {
                    existingVerified.setVerified(false);
                    answerRepository.save(existingVerified);
                });

        // STEP 6 — Mark this answer as verified
        answer.setVerified(true);
        Answer saved = answerRepository.save(answer);

        // STEP 7 — Update question status to VERIFIED
        // This is what makes UI show "Answered" instead of "Pending"
        String previousStatus = question.getStatus().name();
        question.setStatus(Question.QuestionStatus.VERIFIED);
        questionRepository.save(question);

        // STEP 8 — Story 4: Reward Points
        // Add 10 points to the student who gave the verified answer
        // Points are stored in the User entity (already set up by teammate)
        User answerer = answer.getSubmittedBy();
        answerer.setPoints(answerer.getPoints() + 10);
        userRepository.save(answerer);

        // STEP 9 — Observer Pattern: fire status change event
        // AuditListener logs it, NotificationListener can notify the student
        eventPublisher.publishStatusChanged(
                QuestionEvent.statusChanged(question, previousStatus));

        return AnswerResponse.from(saved);
    }

    // ── My Answers ────────────────────────────────────────────────────────────

    @Override
    public List<AnswerResponse> getMyAnswers(String email) {
        User student = getUser(email);
        return answerRepository.findBySubmittedByOrderByCreatedAtDesc(student)
                .stream()
                .map(AnswerResponse::from)
                .collect(Collectors.toList());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    // GRASP — Don't Repeat Yourself: reusable helper to get User
    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    // GRASP — Don't Repeat Yourself: reusable helper to get Question
    private Question getQuestion(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + id));
    }
}