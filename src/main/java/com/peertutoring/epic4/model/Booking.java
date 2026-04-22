package com.peertutoring.epic4.model;

import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * Booking Entity — EPIC 4 (Story 2 & 3)
 *
 * Represents a student booking a tutoring session slot.
 * Links the existing User (student) with the existing TutoringSession.
 *
 * SOLID — SRP: Only holds booking state data.
 *   All booking logic (capacity check, duplicate check) lives in BookingService.
 * entire class holds data it has no methods,no logic,no conditions it just reoresents state of a booking
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "epic4_bookings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"session_id", "student_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private TutoringSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false)
    @Builder.Default
    private boolean attended = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime bookedAt = LocalDateTime.now();
}
