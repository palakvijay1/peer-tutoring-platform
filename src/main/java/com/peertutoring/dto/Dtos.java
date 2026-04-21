package com.peertutoring.dto;

import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.TutorProfile;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * All DTOs for EPIC 1 + EPIC 2 + EPIC 3
 *
 * SOLID — Interface Segregation Principle (ISP):
 *   Each DTO has only the fields needed for that specific operation.
 *   No bloated "god" request/response objects.
 *
 * CREATIONAL — Builder Pattern (Lombok @Builder):
 *   All DTOs use @Builder for clean, readable construction in services.
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

        private String role; // optional, defaults to STUDENT
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
        private Long id;
        private String token;
        private String email;
        private String name;
        private String role;
        private int points;
        private Boolean isTutor;
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
        private String displayStatus;
        private UserResponse postedBy;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private AnswerResponse verifiedAnswer;

        public static QuestionResponse from(Question question) {
            return QuestionResponse.builder()
                    .id(question.getId())
                    .text(question.getText())
                    .subject(question.getSubject().name())
                    .status(question.getStatus().name())
                    .displayStatus(toDisplayStatus(question.getStatus()))
                    .postedBy(UserResponse.from(question.getPostedBy()))
                    .createdAt(question.getCreatedAt())
                    .updatedAt(question.getUpdatedAt())
                    .verifiedAnswer(null)
                    .build();
        }

        private static String toDisplayStatus(Question.QuestionStatus status) {
            return switch (status) {
                case VERIFIED -> "Answered";
                default -> "Pending";
            };
        }
    }

    // ── Answer DTOs ───────────────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubmitAnswerRequest {
        @NotBlank(message = "Answer text is required")
        @Size(min = 5, max = 2000, message = "Answer must be between 5 and 2000 characters")
        private String text;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class VerifyAnswerRequest {
        @NotNull(message = "Answer ID is required")
        private Long answerId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class AnswerResponse {
        private Long id;
        private String text;
        private boolean verified;
        private UserResponse submittedBy;
        private LocalDateTime createdAt;

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

    // ══════════════════════════════════════════════════════════════════════════
    // ── EPIC 3: Tutor + Session DTOs ──────────────────────────────────────────
    // ══════════════════════════════════════════════════════════════════════════

    /**
     * Story 1: Request to register as a tutor.
     * Student sends GPA and bio when applying to become a tutor.
     *
     * SOLID ISP: Only contains fields needed for tutor registration.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RegisterAsTutorRequest {
        @DecimalMin(value = "0.0", message = "CGPA cannot be negative")
        @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
        private double gpa; // CGPA on 10.0 scale; must be > 6.5 to register

        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        private String bio;
    }

    /**
     * Story 1: Response after successful tutor registration.
     * Returns the created TutorProfile data so the frontend can update state.
     *
     * GRASP — Information Expert:
     * TutorProfileResponse knows how to build itself from a TutorProfile entity.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TutorProfileResponse {
        private Long profileId;
        private String tutorName;
        private String tutorEmail;
        private double gpa;
        private String bio;
        private boolean approved;
        private String message;

        public static TutorProfileResponse from(TutorProfile profile, String message) {
            return TutorProfileResponse.builder()
                    .profileId(profile.getId())
                    .tutorName(profile.getUser().getName())
                    .tutorEmail(profile.getUser().getEmail())
                    .gpa(profile.getGpa())
                    .bio(profile.getBio())
                    .approved(profile.isApproved())
                    .message(message)
                    .build();
        }
    }

    /**
     * Story 2: Response for eligibility check.
     * Tells the user if they are eligible and WHY (or why not).
     *
     * SOLID ISP: Only eligibility-related fields here.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class EligibilityResponse {
        private boolean eligible;
        private String criteria;      // what the rule says (e.g., "GPA >= 3.0 AND points >= 50")
        private String currentStatus; // user's current values (e.g., "Your GPA: 3.5, Points: 60")
        private String message;       // final human-readable verdict
    }

    /**
     * Story 3+4: Request to create or update a tutoring session.
     * Contains all session details a tutor provides.
     *
     * SOLID ISP: Only fields a tutor needs when creating a session.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class CreateSessionRequest {
        @NotBlank(message = "Topic is required")
        @Size(min = 3, max = 200, message = "Topic must be between 3 and 200 characters")
        private String topic;

        @NotNull(message = "Subject is required")
        private Question.Subject subject;

        @NotNull(message = "Scheduled time is required")
        private LocalDateTime scheduledAt;

        @Min(value = 1, message = "Capacity must be at least 1")
        @Max(value = 100, message = "Capacity cannot exceed 100")
        private int capacity;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;
    }

    /**
     * Story 3,4,5: Response DTO for session-related operations.
     * Used when creating, updating, or viewing sessions.
     *
     * GRASP — Information Expert:
     * SessionResponse knows how to build itself from a TutoringSession entity.
     */
    @Data @NoArgsConstructor @AllArgsConstructor @Builder(toBuilder = true)
    public static class SessionResponse {
        private Long sessionId;
        private String topic;
        private String subject;
        private LocalDateTime scheduledAt;
        private int capacity;
        private String status;
        private String description;
        private String tutorName;   // who created this session
        private String tutorEmail;
        private LocalDateTime createdAt;
        private String message;     // optional operation feedback

        public static SessionResponse from(TutoringSession session) {
            return SessionResponse.builder()
                    .sessionId(session.getId())
                    .topic(session.getTopic())
                    .subject(session.getSubject().name())
                    .scheduledAt(session.getScheduledAt())
                    .capacity(session.getCapacity())
                    .status(session.getStatus().name())
                    .description(session.getDescription())
                    .tutorName(session.getTutor().getName())
                    .tutorEmail(session.getTutor().getEmail())
                    .createdAt(session.getCreatedAt())
                    .build();
        }
    }
}
