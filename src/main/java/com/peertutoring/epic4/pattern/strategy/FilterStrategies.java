package com.peertutoring.epic4.pattern.strategy;

import com.peertutoring.model.TutoringSession;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/** Returns all sessions unchanged — used when no filter is selected. */
@Component("noFilter")
class NoFilterStrategy implements SessionFilterStrategy {
    @Override
    public List<TutoringSession> filter(List<TutoringSession> sessions, String value) {
        return sessions;
    }
}

/** Filters sessions by subject (case-insensitive match on enum name). */
@Component("subjectFilter")
class SubjectFilterStrategy implements SessionFilterStrategy {
    @Override
    public List<TutoringSession> filter(List<TutoringSession> sessions, String value) {
        return sessions.stream()
                .filter(s -> s.getSubject().name().equalsIgnoreCase(value))
                .collect(Collectors.toList());
    }
}

/** Filters sessions by tutor name (case-insensitive substring match). */
@Component("tutorFilter")
class TutorFilterStrategy implements SessionFilterStrategy {
    @Override
    public List<TutoringSession> filter(List<TutoringSession> sessions, String value) {
        return sessions.stream()
                .filter(s -> s.getTutor().getName().toLowerCase()
                              .contains(value.toLowerCase()))
                .collect(Collectors.toList());
    }
}
