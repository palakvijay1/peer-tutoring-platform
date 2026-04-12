package com.peertutoring.pattern.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ConcreteObserver 2: Audit Listener
 * Records every question event for audit/compliance purposes.
 */
@Slf4j
@Component
public class AuditListener implements QuestionEventListener {

    @Override
    public void onQuestionPosted(QuestionEvent event) {
        // In production: persist to an audit_log table
        log.info("[AUDIT] QUESTION_POSTED | id={} | subject={} | user={} | at={}",
                event.getQuestionId(), event.getSubject(),
                event.getPostedByEmail(), event.getOccurredAt());
    }

    @Override
    public void onStatusChanged(QuestionEvent event) {
        log.info("[AUDIT] STATUS_CHANGED | id={} | {} → {} | at={}",
                event.getQuestionId(), event.getPreviousStatus(),
                event.getNewStatus(), event.getOccurredAt());
    }
}