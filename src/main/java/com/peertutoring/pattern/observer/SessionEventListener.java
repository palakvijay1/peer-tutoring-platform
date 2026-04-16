package com.peertutoring.pattern.observer;

/**
 * ══════════════════════════════════════════════════════════════
 * Observer interface for Session events — EPIC 3
 *
 * Any class that wants to react to session creation/updates
 * must implement this interface.
 *
 * Mirrors QuestionEventListener from teammates, extending the
 * existing Observer infrastructure to cover session events.
 * ══════════════════════════════════════════════════════════════
 */
public interface SessionEventListener {

    /** Called when a new tutoring session is created. */
    void onSessionCreated(SessionEvent event);

    /** Called when a session's details are updated. */
    void onSessionUpdated(SessionEvent event);
}
