package com.peertutoring.epic4.model;

import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * SessionRating Entity — EPIC 4 (Story 4)
 *
 * A student submits a 1-5 rating + optional feedback for a session
 * they attended. The rating is stored and triggers a leaderboard update
 * via the Observer pattern.
 * 
 * OBSERVER PATTERNS EVENT DATA TRIGGERS LEADERBOARD UPDATE WHEN NEW RATING IS CREATED
 SRP STORES RATING DATA NO LEADERBOARD CALC
 * Named "SessionRating" (not "Rating") to avoid any clash with
 * teammates' potential use of a generic Rating name.
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "epic4_ratings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "student_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessionRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TutoringSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    /** The tutor being rated — denormalised for fast leaderboard queries */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    @Column(nullable = false)
    private int score;          // 1–5

    @Column(length = 500)
    private String feedback;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime ratedAt = LocalDateTime.now();
}
