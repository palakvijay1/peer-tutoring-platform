package com.peertutoring.epic4.dto;

import com.peertutoring.epic4.model.Booking;
import com.peertutoring.epic4.model.LeaderboardEntry;
import com.peertutoring.epic4.model.SessionRating;
import com.peertutoring.model.TutoringSession;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * All request/response DTOs for EPIC 4.
 * Kept separate from teammates' Dtos.java to avoid merge conflicts.
 */
public class Epic4Dtos {

    // ── Story 1: Browse Sessions ──────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SessionSummaryResponse {
        private Long sessionId;
        private String topic;
        private String subject;
        private String tutorName;
        private LocalDateTime scheduledAt;
        private int capacity;
        private int bookedCount;
        private int availableSlots;
        private String status;

        public static SessionSummaryResponse from(TutoringSession s, long bookedCount) {
            return SessionSummaryResponse.builder()
                    .sessionId(s.getId())
                    .topic(s.getTopic())
                    .subject(s.getSubject().name())
                    .tutorName(s.getTutor().getName())
                    .scheduledAt(s.getScheduledAt())
                    .capacity(s.getCapacity())
                    .bookedCount((int) bookedCount)
                    .availableSlots((int)(s.getCapacity() - bookedCount))
                    .status(s.getStatus().name())
                    .build();
        }
    }

    // ── Story 2: Book Session ─────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookSessionRequest {
        @NotNull(message = "studentId is required")
        private Long studentId;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class BookingResponse {
        private Long bookingId;
        private Long sessionId;
        private String sessionTopic;
        private String studentName;
        private boolean attended;
        private LocalDateTime bookedAt;
        private String message;

        public static BookingResponse from(Booking b, String message) {
            return BookingResponse.builder()
                    .bookingId(b.getId())
                    .sessionId(b.getSession().getId())
                    .sessionTopic(b.getSession().getTopic())
                    .studentName(b.getStudent().getName())
                    .attended(b.isAttended())
                    .bookedAt(b.getBookedAt())
                    .message(message)
                    .build();
        }
    }

    // ── Story 3: Attend Session ───────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MarkAttendanceRequest {
        @NotNull(message = "studentId is required")
        private Long studentId;
    }

    // ── Story 4: Rate Session ─────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SubmitRatingRequest {
        @NotNull(message = "studentId is required")
        private Long studentId;

        @Min(value = 1, message = "Score must be at least 1")
        @Max(value = 5, message = "Score cannot exceed 5")
        private int score;

        private String feedback;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class RatingResponse {
        private Long ratingId;
        private Long sessionId;
        private String tutorName;
        private String studentName;
        private int score;
        private String feedback;
        private String message;

        public static RatingResponse from(SessionRating r, String message) {
            return RatingResponse.builder()
                    .ratingId(r.getId())
                    .sessionId(r.getSession().getId())
                    .tutorName(r.getTutor().getName())
                    .studentName(r.getStudent().getName())
                    .score(r.getScore())
                    .feedback(r.getFeedback())
                    .message(message)
                    .build();
        }
    }

    // ── Story 5: Leaderboard ──────────────────────────────────────────────────

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class LeaderboardResponse {
        private int rank;
        private String tutorName;
        private String tutorEmail;
        private double averageRating;
        private int totalRatings;

        public static LeaderboardResponse from(LeaderboardEntry e) {
            return LeaderboardResponse.builder()
                    .rank(e.getRank())
                    .tutorName(e.getTutor().getName())
                    .tutorEmail(e.getTutor().getEmail())
                    .averageRating(e.getAverageRating())
                    .totalRatings(e.getTotalRatings())
                    .build();
        }
    }
}
