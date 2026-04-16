package com.peertutoring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * TutorProfile Entity — EPIC 3
 *
 * Stores tutor-specific data: GPA, rating, bio, and approval status.
 * This is separate from User to follow SRP — User handles auth/identity,
 * TutorProfile handles tutor-domain attributes.
 *
 * SOLID — Single Responsibility Principle (SRP):
 * User entity handles identity/auth. TutorProfile handles
 * tutor-domain data. They are separate for clean separation.
 *
 * SOLID — Open/Closed Principle (OCP):
 * New tutor attributes (e.g., subjects of expertise) can be
 * added here without changing User or any other entity.
 *
 * GRASP — Information Expert:
 * TutorProfile is the expert on tutor eligibility data
 * (GPA, rating, points) — it holds the information needed
 * to make eligibility decisions.
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "tutor_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TutorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // One-to-one relationship with User
    // Each tutor user has exactly one TutorProfile
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // GPA on a 4.0 scale — used in eligibility check
    @Column(nullable = false)
    @Builder.Default
    private double gpa = 0.0;

    // Average rating given by students (0.0 - 5.0)
    @Column(nullable = false)
    @Builder.Default
    private double rating = 0.0;

    // Short bio about the tutor's expertise
    @Column(length = 500)
    private String bio;

    // Whether the tutor has been approved (eligible check passed)
    @Column(nullable = false)
    @Builder.Default
    private boolean approved = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}