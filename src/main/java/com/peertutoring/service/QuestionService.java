package com.peertutoring.service;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.model.Question;
import java.util.List;

/**
 * SOLID — Dependency Inversion Principle (DIP):
 * Controllers depend on this interface (abstraction),
 * not on QuestionServiceImpl (concrete class).
 */
public interface QuestionService {

    // ── Epic 1 ────────────────────────────────────────────────────────────────
    QuestionResponse postQuestion(PostQuestionRequest request, String email);
    List<QuestionResponse> getAllQuestions(String sortBy);
    List<QuestionResponse> getMyQuestions(String email);
    QuestionResponse getQuestionById(Long id);
    List<QuestionResponse> getQuestionsBySubject(Question.Subject subject);
    List<String> getAllSubjects();

    // ── Epic 2: Delete Question ───────────────────────────────────────────────
    /**
     * Deletes a question only if the requesting user posted it.
     * GRASP Information Expert: service checks ownership because
     * it has access to both question data and user data.
     */
    void deleteQuestion(Long questionId, String email);

    // ── Epic 2: Get question with verified answer attached ────────────────────
    /**
     * Returns a question with its verified answer attached (if any).
     * Used to display questions with answers in the feed.
     */
    QuestionResponse getQuestionWithAnswer(Long id, String email);
}