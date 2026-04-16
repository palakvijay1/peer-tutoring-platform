package com.peertutoring.pattern.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ══════════════════════════════════════════════════════════════
 * ConcreteObservers for Session Events — EPIC 3
 *
 * Two observers react to session events:
 *   1. SessionAuditListener  — logs every session event for audit trail
 *   2. SessionNotificationListener — simulates sending notifications
 *
 * These mirror AuditListener and NotificationListener from teammates,
 * keeping the codebase consistent.
 *
 * GRASP — Low Coupling: Each listener reacts independently.
 *   Adding or removing a listener doesn't affect others.
 * SOLID — SRP: Each listener has one job.
 * ══════════════════════════════════════════════════════════════
 */

// ── ConcreteObserver 1: Audit Listener ───────────────────────────────────────
@Slf4j
@Component
class SessionAuditListener implements SessionEventListener {

    @Override
    public void onSessionCreated(SessionEvent event) {
        // In production: persist to audit_log table
        log.info("[AUDIT] SESSION_CREATED | id={} | topic={} | subject={} | tutor={} | at={}",
                event.getSessionId(), event.getTopic(), event.getSubject(),
                event.getTutorEmail(), event.getOccurredAt());
    }

    @Override
    public void onSessionUpdated(SessionEvent event) {
        log.info("[AUDIT] SESSION_UPDATED | id={} | topic={} | tutor={} | at={}",
                event.getSessionId(), event.getTopic(),
                event.getTutorEmail(), event.getOccurredAt());
    }
}

// ── ConcreteObserver 2: Notification Listener ────────────────────────────────
@Slf4j
@Component
class SessionNotificationListener implements SessionEventListener {

    @Override
    public void onSessionCreated(SessionEvent event) {
        // In production: send WebSocket push or email to students
        // who follow the subject or tutor
        log.info("[NOTIFICATION] New session created by {} | Topic: {} | Subject: {} | ID: {}",
                event.getTutorEmail(), event.getTopic(),
                event.getSubject(), event.getSessionId());
    }

    @Override
    public void onSessionUpdated(SessionEvent event) {
        log.info("[NOTIFICATION] Session #{} updated by {} | New topic: {}",
                event.getSessionId(), event.getTutorEmail(), event.getTopic());
    }
}
