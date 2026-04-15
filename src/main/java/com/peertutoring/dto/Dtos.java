package com.peertutoring.dto;

import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) for Epic 1 + Epic 2.
 *
 * ══════════════════════════════════════════════════════════════
 * SOLID — Interface Segregation Principle (ISP):
 * Instead of one bloated request/response class, we define lean,
 * purpose-specific DTOs so clients only deal with the fields
 * they actually need.
 *
 * CREATIONAL — Builder Pattern (via Lombok @Builder):
 * All DTOs use @Builder for clean construction in service layer.
 * ══════════════════════════════════════════════════════════════
 */
public class Dtos {

    // ── Auth DTOs ─────────────────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SignupRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        // Role field — optional, defaults to STUDENT if not provided
        // Faculty can signup by passing role = "FACULTY"
        private String role;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AuthResponse {
        private String token;
        private String email;
        private String name;
        private String role;
        private int points;
        private String message;
    }

    // ── User DTOs ─────────────────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserResponse {
        private Long id;
        private String name;
        private String email;
        private String role;
        private int points;
        private LocalDateTime createdAt;

        public static UserResponse from(User user) {
            return UserResponse.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                    .points(user.getPoints())
                    .createdAt(user.getCreatedAt())
                    .build();
        }
    }

    // ── Question DTOs ─────────────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class PostQuestionRequest {
        @NotBlank(message = "Question text is required")
        @Size(min = 10, max = 1000, message = "Question must be between 10 and 1000 characters")
        private String text;

        @NotNull(message = "Subject is required")
        private Question.Subject subject;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionResponse {
        private Long id;
        private String text;
        private String subject;
        private String status;

        // UI label — maps internal status to what student sees
        // PENDING   → "Pending"
        // ANSWERED  → "Pending"  (answers exist but not verified yet)
        // VERIFIED  → "Answered" (faculty verified, correct answer visible)
        private String displayStatus;

        private UserResponse postedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // The verified answer (only present when status = VERIFIED)
        // null means no verified answer yet — UI hides answer section
        private AnswerResponse verifiedAnswer;

        public static QuestionResponse from(Question question) {
            return QuestionResponse.builder()
                    .id(question.getId())
                    .text(question.getText())
                    .subject(question.getSubject().name())
                    .status(question.getStatus().name())
                    // Map internal state to UI label
                    .displayStatus(toDisplayStatus(question.getStatus()))
                    .postedBy(UserResponse.from(question.getPostedBy()))
                    .createdAt(question.getCreatedAt())
                    .updatedAt(question.getUpdatedAt())
                    .verifiedAnswer(null) // service sets this separately
                    .build();
        }

        /**
         * Maps internal QuestionStatus to what the UI should display.
         *
         * SOLID — OCP: If we add a new internal state later,
         * we only update this method — nothing else changes.
         *
         * PENDING  → "Pending"  (no answers yet)
         * ANSWERED → "Pending"  (answers exist, faculty hasn't verified)
         * VERIFIED → "Answered" (faculty verified a correct answer)
         */
        private static String toDisplayStatus(Question.QuestionStatus status) {
            return switch (status) {
                case VERIFIED -> "Answered";
                default -> "Pending";
            };
        }
    }

    // ── Answer DTOs ───────────────────────────────────────────────────────────

    /**
     * Request DTO for submitting an answer.
     * SOLID ISP: Only contains the fields a student needs to submit.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubmitAnswerRequest {
        @NotBlank(message = "Answer text is required")
        @Size(min = 5, max = 2000, message = "Answer must be between 5 and 2000 characters")
        private String text;
    }

    /**
     * Request DTO for faculty to verify an answer.
     * SOLID ISP: Only contains the answer ID faculty needs to verify.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VerifyAnswerRequest {
        @NotNull(message = "Answer ID is required")
        private Long answerId;
    }

    /**
     * Response DTO for an answer.
     * Used when displaying answers under a question.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AnswerResponse {
        private Long id;
        private String text;
        private boolean verified;
        private UserResponse submittedBy;
        private LocalDateTime createdAt;

        // GRASP — Information Expert:
        // AnswerResponse knows how to build itself from an Answer entity
        public static AnswerResponse from(Answer answer) {
            return AnswerResponse.builder()
                    .id(answer.getId())
                    .text(answer.getText())
                    .verified(answer.isVerified())
                    .submittedBy(UserResponse.from(answer.getSubmittedBy()))
                    .createdAt(answer.getCreatedAt())
                    .build();
        }
    }

    // ── Generic API Response wrapper ──────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;

        public static <T> ApiResponse<T> ok(String message, T data) {
            return ApiResponse.<T>builder().success(true).message(message).data(data).build();
        }

        public static <T> ApiResponse<T> error(String message) {
            return ApiResponse.<T>builder().success(false).message(message).build();
        }
    }
}