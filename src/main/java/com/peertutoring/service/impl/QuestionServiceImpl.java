package com.peertutoring.service.impl;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import com.peertutoring.pattern.observer.*;
import com.peertutoring.pattern.strategy.QuestionSortStrategy;
import com.peertutoring.repository.AnswerRepository;
import com.peertutoring.repository.QuestionRepository;
import com.peertutoring.repository.UserRepository;
import com.peertutoring.service.QuestionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ══════════════════════════════════════════════════════════════
 * Design Patterns used here:
 *   - Observer: publishes question events to listeners
 *   - Strategy: delegates sorting algorithm based on caller's choice
 *
 * Design Principles:
 *   - SRP: only handles question-related business logic
 *   - OCP: new sort strategies or listeners can be added
 *          without touching this class
 *   - DIP: depends on interfaces not concrete classes
 * ══════════════════════════════════════════════════════════════
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;         // Added for Epic 2
    private final QuestionEventPublisher eventPublisher;
    private final NotificationListener notificationListener;
    private final AuditListener auditListener;
    private final Map<String, QuestionSortStrategy> sortStrategies;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            UserRepository userRepository,
            AnswerRepository answerRepository,
            QuestionEventPublisher eventPublisher,
            NotificationListener notificationListener,
            AuditListener auditListener,
            @Qualifier("sortByNewest") QuestionSortStrategy sortByNewest,
            @Qualifier("sortBySubject") QuestionSortStrategy sortBySubject,
            @Qualifier("sortByStatus") QuestionSortStrategy sortByStatus) {

        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
        this.eventPublisher = eventPublisher;
        this.notificationListener = notificationListener;
        this.auditListener = auditListener;
        this.sortStrategies = Map.of(
                "newest", sortByNewest,
                "subject", sortBySubject,
                "status", sortByStatus
        );
    }

    @PostConstruct
    public void registerObservers() {
        eventPublisher.subscribe(notificationListener);
        eventPublisher.subscribe(auditListener);
    }

    // ── Epic 1: Post Question ─────────────────────────────────────────────────

    @SuppressWarnings("null")
    @Override
    public QuestionResponse postQuestion(PostQuestionRequest request, String email) {
        User user = getUser(email);

        Question question = Question.builder()
                .text(request.getText())
                .subject(request.getSubject())
                .postedBy(user)
                .build();

        Question saved = questionRepository.save(question);
        eventPublisher.publishQuestionPosted(QuestionEvent.posted(saved));
        return QuestionResponse.from(saved);
    }

    // ── Epic 1: Get All Questions ─────────────────────────────────────────────

    @Override
    public List<QuestionResponse> getAllQuestions(String sortBy) {
        List<Question> questions = questionRepository.findAllByOrderByCreatedAtDesc();

        QuestionSortStrategy strategy = sortStrategies.getOrDefault(
                sortBy != null ? sortBy.toLowerCase() : "newest",
                sortStrategies.get("newest"));

        return strategy.sort(questions).stream()
                .map(q -> attachVerifiedAnswer(QuestionResponse.from(q), q))
                .collect(Collectors.toList());
    }

    // ── Epic 1: Get My Questions ──────────────────────────────────────────────

    @Override
    public List<QuestionResponse> getMyQuestions(String email) {
        User user = getUser(email);
        return questionRepository.findByPostedByOrderByCreatedAtDesc(user)
                .stream()
                .map(q -> attachVerifiedAnswer(QuestionResponse.from(q), q))
                .collect(Collectors.toList());
    }

    // ── Epic 1: Get Question By ID ────────────────────────────────────────────

    @SuppressWarnings("null")
    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + id));
        return attachVerifiedAnswer(QuestionResponse.from(question), question);
    }

    // ── Epic 2: Get Question With Answer ──────────────────────────────────────

    @Override
    public QuestionResponse getQuestionWithAnswer(Long id, String email) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + id));
        return attachVerifiedAnswer(QuestionResponse.from(question), question);
    }

    // ── Epic 1: Get Questions By Subject ──────────────────────────────────────

    @Override
    public List<QuestionResponse> getQuestionsBySubject(Question.Subject subject) {
        return questionRepository.findBySubject(subject)
                .stream()
                .map(q -> attachVerifiedAnswer(QuestionResponse.from(q), q))
                .collect(Collectors.toList());
    }

    // ── Epic 1: Get All Subjects ──────────────────────────────────────────────

    @Override
    public List<String> getAllSubjects() {
        return Arrays.stream(Question.Subject.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    // ── Epic 2: Delete Question ───────────────────────────────────────────────

    @Override
    public void deleteQuestion(Long questionId, String email) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + questionId));

        // Only the person who posted it can delete it
        // GRASP Information Expert: service enforces ownership rule
        if (!question.getPostedBy().getEmail().equalsIgnoreCase(email)) {
            throw new ResourceNotFoundException(
                    "You are not authorized to delete this question.");
        }

        questionRepository.deleteById(questionId);

        // Observer: notify listeners that question was removed
        eventPublisher.publishStatusChanged(
                QuestionEvent.statusChanged(question, question.getStatus().name()));
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    /**
     * Attaches the verified answer to a QuestionResponse if one exists.
     * This is what makes the verified answer show up under the question
     * in the student feed.
     *
     * GRASP — Information Expert: QuestionServiceImpl has access to
     * AnswerRepository, so it can fetch and attach verified answers.
     */
    private QuestionResponse attachVerifiedAnswer(QuestionResponse response, Question question) {
        if (question.getStatus() == Question.QuestionStatus.VERIFIED) {
            Optional<Answer> verifiedAnswer =
                    answerRepository.findByQuestionAndVerifiedTrue(question);
            verifiedAnswer.ifPresent(answer ->
                    response.setVerifiedAnswer(AnswerResponse.from(answer)));
        }
        return response;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }
}