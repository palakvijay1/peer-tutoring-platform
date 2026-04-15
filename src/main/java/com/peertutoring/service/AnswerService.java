package com.peertutoring.service;

import com.peertutoring.dto.Dtos.*;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * SOLID — Dependency Inversion Principle (DIP):
 * AnswerController depends on THIS interface (abstraction),
 * not on AnswerServiceImpl (concrete class).
 * This means we can swap implementations without touching
 * the controller at all.
 *
 * SOLID — Interface Segregation Principle (ISP):
 * Only answer-related methods live here.
 * Question methods stay in QuestionService.
 * ══════════════════════════════════════════════════════════════
 */
public interface AnswerService {

    /**
     * Story 1: Submit Answer
     * Student submits an answer to a question.
     * Only students who did NOT post the question can answer.
     *
     * @param questionId the question being answered
     * @param request    contains the answer text
     * @param email      the student's email (from X-User-Email header)
     */
    AnswerResponse submitAnswer(Long questionId, SubmitAnswerRequest request, String email);

    /**
     * Story 2: View Answers for a question
     * Returns all answers for a question.
     * Students see only verified answers.
     * Faculty sees all answers (to decide which to verify).
     *
     * @param questionId the question whose answers to fetch
     * @param email      the requester's email (to check role)
     */
    List<AnswerResponse> getAnswersForQuestion(Long questionId, String email);

    /**
     * Story 3: Verify Answer (Faculty only)
     * Faculty marks one answer as the correct/verified answer.
     * This also updates the question status to VERIFIED.
     * Reward points are added to the answering student.
     *
     * @param questionId the question whose answer is being verified
     * @param request    contains the answer ID to verify
     * @param email      faculty email (role check happens in service)
     */
    AnswerResponse verifyAnswer(Long questionId, VerifyAnswerRequest request, String email);

    /**
     * My Answers — student views all answers they personally submitted
     *
     * @param email the student's email
     */
    List<AnswerResponse> getMyAnswers(String email);
}