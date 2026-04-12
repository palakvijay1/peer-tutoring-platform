package com.peertutoring.pattern.observer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * ConcreteObserver 1: Notification Listener
 * In a real system this would send an email/push notification.
 * Here it logs the event to demonstrate the pattern.
 */
@Slf4j
@Component
public class NotificationListener implements QuestionEventListener {

    @Override
    public void onQuestionPosted(QuestionEvent event) {
        // In production: send email/WebSocket push to relevant tutors
        log.info("[NOTIFICATION] New question posted by {} | Subject: {} | ID: {}",
                event.getPostedByEmail(), event.getSubject(), event.getQuestionId());
    }

    @Override
    public void onStatusChanged(QuestionEvent event) {
        log.info("[NOTIFICATION] Question #{} status changed: {} → {}",
                event.getQuestionId(), event.getPreviousStatus(), event.getNewStatus());
    }
}