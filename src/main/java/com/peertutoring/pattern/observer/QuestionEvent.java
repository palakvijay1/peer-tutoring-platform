package com.peertutoring.pattern.observer;

import com.peertutoring.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

/** Immutable event object passed to all listeners. */
@Data
@AllArgsConstructor
public class QuestionEvent {
    private Long questionId;
    private String questionText;
    private String subject;
    private String previousStatus;
    private String newStatus;
    private String postedByEmail;
    private LocalDateTime occurredAt;

    /** Convenience constructor for a new-question event. */
    public static QuestionEvent posted(Question q) {
        return new QuestionEvent(
                q.getId(),
                q.getText(),
                q.getSubject().name(),
                null,
                q.getStatus().name(),
                q.getPostedBy().getEmail(),
                LocalDateTime.now()
        );
    }

    /** Convenience constructor for a status-change event. */
    public static QuestionEvent statusChanged(Question q, String previousStatus) {
        return new QuestionEvent(
                q.getId(),
                q.getText(),
                q.getSubject().name(),
                previousStatus,
                q.getStatus().name(),
                q.getPostedBy().getEmail(),
                LocalDateTime.now()
        );
    }
}