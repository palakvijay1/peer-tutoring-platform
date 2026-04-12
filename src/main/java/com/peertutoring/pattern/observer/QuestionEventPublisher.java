package com.peertutoring.pattern.observer;

import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Subject / Publisher — manages listeners and fires events.
 */
@Component
public class QuestionEventPublisher {

    private final List<QuestionEventListener> listeners = new ArrayList<>();

    public void subscribe(QuestionEventListener listener) {
        listeners.add(listener);
    }

    public void unsubscribe(QuestionEventListener listener) {
        listeners.remove(listener);
    }

    public void publishQuestionPosted(QuestionEvent event) {
        listeners.forEach(l -> l.onQuestionPosted(event));
    }

    public void publishStatusChanged(QuestionEvent event) {
        listeners.forEach(l -> l.onStatusChanged(event));
    }
}