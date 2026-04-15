package com.peertutoring.controller;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * SOLID — Single Responsibility Principle (SRP):
 * This controller only handles HTTP concerns for answers.
 * It receives requests, calls the service, returns responses.
 * Zero business logic lives here.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 * Depends on AnswerService (interface/abstraction),
 * not AnswerServiceImpl (concrete class).
 *
 * CREATIONAL — Singleton Pattern:
 * @RestController makes Spring create ONE instance of this
 * controller and reuse it for all incoming requests.
 *
 * GRASP — Controller:
 * This is the GRASP Controller pattern — a dedicated class
 * that handles system events (HTTP requests) and delegates
 * to the appropriate service.
 * ══════════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    /**
     * POST /api/answers/{questionId}
     * Story 1: Student submits an answer to a question
     */
    @PostMapping("/{questionId}")
    public ResponseEntity<ApiResponse<AnswerResponse>> submitAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody SubmitAnswerRequest request,
            @RequestHeader("X-User-Email") String email) {

        AnswerResponse response = answerService.submitAnswer(questionId, request, email);
        return ResponseEntity.ok(ApiResponse.ok("Answer submitted successfully!", response));
    }

    /**
     * GET /api/answers/{questionId}
     * Story 2: Get answers for a question
     * Faculty sees all answers, students see only verified answer
     */
    @GetMapping("/{questionId}")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getAnswers(
            @PathVariable Long questionId,
            @RequestHeader("X-User-Email") String email) {

        List<AnswerResponse> answers = answerService.getAnswersForQuestion(questionId, email);
        return ResponseEntity.ok(ApiResponse.ok("Answers fetched!", answers));
    }

    /**
     * POST /api/answers/{questionId}/verify
     * Story 3: Faculty verifies the correct answer
     */
    @PostMapping("/{questionId}/verify")
    public ResponseEntity<ApiResponse<AnswerResponse>> verifyAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody VerifyAnswerRequest request,
            @RequestHeader("X-User-Email") String email) {

        AnswerResponse response = answerService.verifyAnswer(questionId, request, email);
        return ResponseEntity.ok(ApiResponse.ok("Answer verified successfully!", response));
    }

    /**
     * GET /api/answers/my
     * Student views all answers they personally submitted
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<AnswerResponse>>> getMyAnswers(
            @RequestHeader("X-User-Email") String email) {

        List<AnswerResponse> answers = answerService.getMyAnswers(email);
        return ResponseEntity.ok(ApiResponse.ok("Your answers!", answers));
    }
}