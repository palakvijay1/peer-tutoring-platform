package com.peertutoring.pattern.observer;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Observer — Subject/Publisher — EPIC 3
 * ══════════════════════════════════════════════════════════════
 *
 * SessionEventPublisher is the Subject in the Observer pattern.
 * It maintains a list of listeners and notifies all of them
 * when a session event occurs.
 *
 * This mirrors QuestionEventPublisher from teammates, keeping
 * the architecture consistent across all three EPICs.
 *
 * SOLID — OCP: New listeners can be added (subscribed) without
 *   modifying this publisher class.
 * GRASP — Low Coupling: Publisher doesn't know or care what
 *   listeners do — it just notifies them.
 * ══════════════════════════════════════════════════════════════
 */
@Component
public class SessionEventPublisher {

    private final List<SessionEventListener> listeners = new ArrayList<>();

    public void subscribe(SessionEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(SessionEventListener listener) {
        listeners.remove(listener);
    }

    public void publishSessionCreated(SessionEvent event) {
        listeners.forEach(l -> l.onSessionCreated(event));
    }

    public void publishSessionUpdated(SessionEvent event) {
        listeners.forEach(l -> l.onSessionUpdated(event));
    }
}
