package com.peertutoring.epic4.service;

import com.peertutoring.epic4.dto.Epic4Dtos.SessionSummaryResponse;
import com.peertutoring.epic4.pattern.strategy.SessionFilterStrategy;
import com.peertutoring.epic4.repository.BookingRepository;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.repository.TutoringSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Story 1 — Browse Sessions.
 *
 * Uses the Strategy pattern to apply filters.
 * Spring auto-injects all SessionFilterStrategy beans into the map
 * keyed by bean name ("noFilter", "subjectFilter", "tutorFilter").
 */
@Service
@RequiredArgsConstructor
public class SessionBrowseService {

    private final TutoringSessionRepository sessionRepository;
    private final BookingRepository bookingRepository;

    // Strategy Pattern: map of filter name → strategy implementation
    private final Map<String, SessionFilterStrategy> filterStrategies;

    /**
     * Returns all UPCOMING sessions, optionally filtered.
     *
     * @param filterType  "subjectFilter" | "tutorFilter" | null
     * @param filterValue the value to filter on (e.g. "MATH", "Alice")
     */
    public List<SessionSummaryResponse> browseSessions(String filterType, String filterValue) {
        List<TutoringSession> sessions =
                sessionRepository.findByStatusOrderByScheduledAtAsc(
                        TutoringSession.SessionStatus.UPCOMING);

        // Pick strategy — fall back to noFilter if type unrecognised or null
        String key = (filterType != null && filterStrategies.containsKey(filterType))
                ? filterType : "noFilter";

        sessions = filterStrategies.get(key).filter(sessions, filterValue);

        return sessions.stream()
                .map(s -> SessionSummaryResponse.from(s, bookingRepository.countBySession(s)))
                .collect(Collectors.toList());
    }
}
