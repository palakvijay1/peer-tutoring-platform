package com.peertutoring.dto;

import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * DTOs (Data Transfer Objects) for Epic 1.
 *
 * Design Principle Applied: Interface Segregation Principle (ISP)
 *   Instead of one bloated request/response class, we define lean,
 *   purpose-specific DTOs so clients only deal with the fields they need.
 */
public class Dtos {

    // ── Auth DTOs ────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SignupRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AuthResponse {
        private String token;
        private String email;
        private String name;
        private String role;
        private String message;
    }

    // ── User DTOs ─────────────────────────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostQuestionRequest {
        @NotBlank(message = "Question text is required")
        @Size(min = 10, max = 1000, message = "Question must be between 10 and 1000 characters")
        private String text;

        @NotNull(message = "Subject is required")
        private Question.Subject subject;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionResponse {
        private Long id;
        private String text;
        private String subject;
        private String status;
        private UserResponse postedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public static QuestionResponse from(Question question) {
            return QuestionResponse.builder()
                    .id(question.getId())
                    .text(question.getText())
                    .subject(question.getSubject().name())
                    .status(question.getStatus().name())
                    .postedBy(UserResponse.from(question.getPostedBy()))
                    .createdAt(question.getCreatedAt())
                    .updatedAt(question.getUpdatedAt())
                    .build();
        }
    }

    // ── Generic API Response wrapper ──────────────────────────────────────────

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
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