package com.peertutoring.pattern.observer;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Observer — Listener Interface
 *
 * This interface was MISSING from your project, which causes
 * AuditListener and NotificationListener (Epic 1/2) to fail.
 *
 * Any class that wants to react to question events must implement this.
 * AuditListener and NotificationListener are ConcreteObservers.
 *
 * SOLID — OCP: New listeners can be added without changing existing ones.
 * GRASP — Low Coupling: Publisher depends only on this interface.
 * ══════════════════════════════════════════════════════════════
 */
public interface QuestionEventListener {

    /** Called when a new question is posted. */
    void onQuestionPosted(QuestionEvent event);

    /** Called when a question's status changes (e.g., PENDING → ANSWERED). */
    void onStatusChanged(QuestionEvent event);
}
