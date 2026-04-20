package com.peertutoring.epic4.pattern.factory;

import com.peertutoring.epic4.model.Booking;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import org.springframework.stereotype.Component;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Factory Method — EPIC 4 (Creational)
 * ══════════════════════════════════════════════════════════════
 *
 * WHY chosen:
 *   Creating a Booking requires consistent initialisation:
 *   attended = false, bookedAt = now(), plus validation that
 *   both session and student are non-null.
 *   Scattering `new Booking(...)` calls across BookingService
 *   would violate SRP and make future field additions risky.
 *
 * HOW applied:
 *   BookingService calls BookingFactory.createBooking(session, student).
 *   The factory handles all Booking construction details.
 *   This is the same pattern teammates used for UserFactory,
 *   AnswerFactory, and SessionFactory — keeping architecture consistent.
 *
 * DESIGN PRINCIPLE — SRP (Single Responsibility Principle):
 *   This class has exactly one job: construct Booking objects.
 *   If Booking gains new fields (e.g., meetingLink, reminderSent),
 *   only BookingFactory changes — not BookingService.
 *
 * WHERE used:
 *   BookingService.bookSession() delegates construction here.
 * ══════════════════════════════════════════════════════════════
 */
@Component
public class BookingFactory {

    /**
     * Creates a fresh Booking with attended = false.
     * Called when a student confirms a slot.
     */
    public Booking createBooking(TutoringSession session, User student) {
        if (session == null) throw new IllegalArgumentException("Session must not be null");
        if (student == null) throw new IllegalArgumentException("Student must not be null");

        return Booking.builder()
                .session(session)
                .student(student)
                .attended(false)
                .build();
    }
}
