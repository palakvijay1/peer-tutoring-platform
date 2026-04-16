package com.peertutoring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * TutoringSession Entity — EPIC 3
 *
 * Represents a tutoring session created by a Tutor.
 * A session has a topic, scheduled time, max capacity,
 * and is linked to the tutor who created it.
 *
 * SOLID — Single Responsibility Principle (SRP):
 * This class only holds session data. All business logic
 * (eligibility check, creation rules) lives in the service layer.
 *
 * SOLID — Open/Closed Principle (OCP):
 * SessionStatus enum can be extended with new states
 * (e.g., CANCELLED, COMPLETED) without modifying this class.
 *
 * CREATIONAL — Builder Pattern (Lombok @Builder):
 * Consistent with how teammates built User, Question, Answer.
 * Allows clean construction:
 * TutoringSession.builder().topic(...).tutor(...).build()
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "tutoring_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutoringSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The topic this session covers (e.g., "Binary Trees", "Calculus")
    @Column(nullable = false, length = 200)
    private String topic;

    // Subject area — reuses existing Subject enum from Question model
    // GRASP Low Coupling: reuse existing enum instead of duplicating
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Question.Subject subject;

    // When this session is scheduled to happen
    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    // How many students can join this session
    @Column(nullable = false)
    private int capacity;

    // Status of the session
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private SessionStatus status = SessionStatus.UPCOMING;

    // The tutor who created this session
    // GRASP Information Expert: TutoringSession knows who owns it
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", nullable = false)
    private User tutor;

    // Optional description / notes about this session
    @Column(length = 500)
    private String description;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Session lifecycle states.
     * OCP: add CANCELLED or COMPLETED here without touching any other code.
     */
    public enum SessionStatus {
        UPCOMING, // Session is scheduled but hasn't started
        ACTIVE, // Session is currently in progress
        COMPLETED // Session has ended
    }
}