package com.peertutoring.pattern.observer;

import com.peertutoring.model.TutoringSession;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Observer — Event Object — EPIC 3
 *
 * Immutable event object passed to all session listeners when
 * a session is created or updated.
 *
 * FIX: Added static factory methods created() and updated()
 * so TutorServiceImpl can build events cleanly from a session object.
 * Also kept @Builder for flexibility.
 * ══════════════════════════════════════════════════════════════
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionEvent {

    private Long sessionId;
    private String topic;
    private String subject;
    private String tutorEmail;
    private String eventType;      // "SESSION_CREATED" or "SESSION_UPDATED"
    private LocalDateTime occurredAt;

    /**
     * Static factory — called when a session is first created.
     * GRASP Information Expert: SessionEvent knows how to build itself
     * from a TutoringSession.
     */
    public static SessionEvent created(TutoringSession session) {
        return SessionEvent.builder()
                .sessionId(session.getId())
                .topic(session.getTopic())
                .subject(session.getSubject().name())
                .tutorEmail(session.getTutor().getEmail())
                .eventType("SESSION_CREATED")
                .occurredAt(LocalDateTime.now())
                .build();
    }

    /**
     * Static factory — called when a session is updated.
     */
    public static SessionEvent updated(TutoringSession session) {
        return SessionEvent.builder()
                .sessionId(session.getId())
                .topic(session.getTopic())
                .subject(session.getSubject().name())
                .tutorEmail(session.getTutor().getEmail())
                .eventType("SESSION_UPDATED")
                .occurredAt(LocalDateTime.now())
                .build();
    }
}
