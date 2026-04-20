package com.peertutoring.epic4.pattern.strategy;

import com.peertutoring.model.TutoringSession;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Strategy (Behavioral) — EPIC 4
 * ══════════════════════════════════════════════════════════════
 *
 * WHY chosen:
 *   Story 1 requires students to browse sessions with filters
 *   (by subject, by tutor, or unfiltered). Hard-coding if/else
 *   chains in SessionBrowseService for each filter type violates
 *   SRP and makes adding new filters (e.g., by date, by rating)
 *   risky — you'd have to edit the service every time.
 *
 *   Strategy encapsulates each filter algorithm as a separate class.
 *   SessionBrowseService picks the right one at runtime via a map.
 *
 * HOW applied:
 *   - SessionFilterStrategy = Strategy interface
 *   - SubjectFilterStrategy, TutorFilterStrategy, NoFilterStrategy
 *     = concrete strategies, each a Spring @Component
 *   - SessionBrowseService injects Map<String, SessionFilterStrategy>
 *     and looks up the right strategy by name
 *
 * DESIGN PRINCIPLE — ISP (Interface Segregation Principle):
 *   Each strategy implements only one small method: filter().
 *   No concrete class is forced to implement methods it doesn't need.
 *   This is distinct from teammates' EligibilityStrategy (also Strategy
 *   pattern) — that one is for tutor eligibility, this one is purely
 *   for browsing/filtering sessions.
 * ══════════════════════════════════════════════════════════════
 */
public interface SessionFilterStrategy {
    /**
     * @param sessions full list of upcoming sessions
     * @param value    the filter value (subject name, tutor name, etc.)
     * @return filtered subset
     */
    List<TutoringSession> filter(List<TutoringSession> sessions, String value);
}
