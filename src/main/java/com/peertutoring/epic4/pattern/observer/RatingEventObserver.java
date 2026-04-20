package com.peertutoring.epic4.pattern.observer;

import com.peertutoring.model.User;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Observer (Behavioral) — EPIC 4
 * ══════════════════════════════════════════════════════════════
 *
 * WHY chosen:
 *   When a student submits a rating, the leaderboard must update
 *   automatically. Coupling RatingService directly to LeaderboardService
 *   would violate OCP — every new "reaction to a rating" would require
 *   editing RatingService.
 *
 *   Observer decouples the event source (RatingService) from
 *   all reactions (LeaderboardUpdater, and any future ones).
 *
 * HOW applied:
 *   RatingService holds a list of RatingEventObserver beans.
 *   After saving a rating it calls notifyObservers(tutor).
 *   LeaderboardUpdater is the only concrete observer right now,
 *   but more can be added (e.g. email, badge award) without
 *   touching RatingService.
 *
 * DESIGN PRINCIPLE — OCP (Open/Closed Principle):
 *   RatingService is closed for modification but open for extension:
 *   register a new RatingEventObserver bean and it gets called
 *   automatically — zero changes to existing code.
 * ══════════════════════════════════════════════════════════════
 */
public interface RatingEventObserver {
    void onRatingSubmitted(User tutor);
}
