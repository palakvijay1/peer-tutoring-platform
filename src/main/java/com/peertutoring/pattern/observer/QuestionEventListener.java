package com.peertutoring.pattern.observer;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Observer (Behavioral)
 * ══════════════════════════════════════════════════════════════
 *
 * WHY: When a question is posted or its status changes, multiple subsystems
 *      need to react — e.g., send a notification, log an audit event, update
 *      analytics. Hard-coding these reactions into QuestionService creates
 *      tight coupling. The Observer pattern decouples the event source
 *      (QuestionService) from its listeners.
 *
 * HOW IT APPLIES HERE:
 *   - QuestionEventPublisher is the Subject.
 *   - QuestionEventListener is the Observer interface.
 *   - NotificationListener and AuditListener are ConcreteObservers.
 *   - When a question is posted, the publisher notifies all registered listeners.
 *
 * HOW TO SHOW IN REPORT:
 *   "The Observer pattern decouples the question-posting logic from downstream
 *    reactions like notifications and audit logging. New listeners can be added
 *    without modifying QuestionService, satisfying OCP."
 */

// ── Observer interface ────────────────────────────────────────────────────────
public interface QuestionEventListener {

    /** Called when a new question is posted. */
    void onQuestionPosted(QuestionEvent event);

    /** Called when a question's status changes. */
    void onStatusChanged(QuestionEvent event);
}