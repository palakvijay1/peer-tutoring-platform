package com.peertutoring.pattern.factory;

import com.peertutoring.model.Question;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Factory — EPIC 3
 * ══════════════════════════════════════════════════════════════
 *
 * WHY this pattern is chosen:
 *   Creating a TutoringSession requires setting multiple fields
 *   correctly (topic, subject, time, capacity, tutor, status).
 *   If we scattered this construction logic across the service,
 *   it would be duplicated and hard to maintain.
 *   The Factory centralises all TutoringSession creation in ONE place.
 *
 * HOW it is applied:
 *   - TutorServiceImpl calls SessionFactory.createSession(...)
 *   - SessionFactory handles all construction details
 *   - Consistent with AnswerFactory and UserFactory from teammates
 *
 * WHERE it is used:
 *   TutorServiceImpl.createSession() delegates to this factory.
 *
 * BENEFITS:
 *   1. If TutoringSession gets a new required field, update factory only
 *   2. Keeps service code clean and readable
 *   3. Easier to unit test construction logic separately
 *   4. Follows the same pattern as AnswerFactory (teammate's work)
 *
 * SOLID — SRP: This class has one job — create TutoringSession objects.
 * CREATIONAL — Singleton: @Component = one Spring bean instance.
 * ══════════════════════════════════════════════════════════════
 */
@Component
public class SessionFactory {

    /**
     * Creates a standard upcoming tutoring session.
     * Status defaults to UPCOMING — can be changed by tutor later.
     *
     * @param topic       the topic/title of the session
     * @param subject     the subject area (reuses Question.Subject enum)
     * @param scheduledAt when the session will take place
     * @param capacity    maximum number of students who can attend
     * @param description optional additional notes about the session
     * @param tutor       the User who is creating this session
     */
    public TutoringSession createSession(
            String topic,
            Question.Subject subject,
            LocalDateTime scheduledAt,
            int capacity,
            String description,
            User tutor) {

        // Builder pattern — consistent with how teammates create entities
        return TutoringSession.builder()
                .topic(topic)
                .subject(subject)
                .scheduledAt(scheduledAt)
                .capacity(capacity)
                .description(description)
                .tutor(tutor)
                .status(TutoringSession.SessionStatus.UPCOMING) // always starts UPCOMING
                .build();
    }
}
