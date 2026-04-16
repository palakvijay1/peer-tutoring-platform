package com.peertutoring.pattern.strategy;

import com.peertutoring.model.TutorProfile;
import com.peertutoring.model.User;
import org.springframework.stereotype.Component;

/**
 * ══════════════════════════════════════════════════════════════
 * Concrete Strategy implementations for Eligibility Checking.
 * Each class is a separate, testable eligibility rule.
 *
 * SOLID — SRP: Each class has exactly one responsibility (one rule).
 * SOLID — OCP: Add a new rule by adding a new class here.
 * GRASP — Low Coupling: Strategies don't depend on each other.
 * ══════════════════════════════════════════════════════════════
 */

// ── Strategy 1: GPA-based eligibility ────────────────────────────────────────
/**
 * A student is eligible if their GPA is 3.0 or above (on a 4.0 scale).
 * This is the most common academic eligibility criterion.
 */
@Component("gpaEligibilityStrategy")
class GpaEligibilityStrategy implements EligibilityStrategy {

    private static final double MIN_GPA = 3.0;

    @Override
    public boolean isEligible(User user, TutorProfile profile) {
        // GPA must be at or above the minimum threshold
        return profile.getGpa() >= MIN_GPA;
    }

    @Override
    public String getEligibilityCriteria() {
        return "GPA must be at least " + MIN_GPA + " out of 4.0";
    }
}

// ── Strategy 2: Points-based eligibility
// ──────────────────────────────────────
/**
 * A student is eligible if they have earned 50 or more points
 * by answering questions on the platform.
 * Points are already tracked in the User entity by teammates.
 */
@Component("pointsEligibilityStrategy")
class PointsEligibilityStrategy implements EligibilityStrategy {

    private static final int MIN_POINTS = 50;

    @Override
    public boolean isEligible(User user, TutorProfile profile) {
        // User points are stored in the existing User entity (from teammate's work)
        return user.getPoints() >= MIN_POINTS;
    }

    @Override
    public String getEligibilityCriteria() {
        return "Must have earned at least " + MIN_POINTS + " points on the platform";
    }
}

// ── Strategy 3: Rating-based eligibility
// ──────────────────────────────────────
/**
 * A student is eligible if their tutor profile rating is 4.0 or above.
 * This applies to tutors who want to continue tutoring (renewal check).
 */
@Component("ratingEligibilityStrategy")
class RatingEligibilityStrategy implements EligibilityStrategy {

    private static final double MIN_RATING = 4.0;

    @Override
    public boolean isEligible(User user, TutorProfile profile) {
        // If no rating yet (0.0), we allow it (first-time tutors have no rating)
        if (profile.getRating() == 0.0)
            return true;
        return profile.getRating() >= MIN_RATING;
    }

    @Override
    public String getEligibilityCriteria() {
        return "Rating must be at least " + MIN_RATING + " out of 5.0 (if rated)";
    }
}

// ── Strategy 4: Combined eligibility (GPA + Points)
// ───────────────────────────
/**
 * The default strategy used in production.
 * A student is eligible only if BOTH GPA >= 3.0 AND points >= 50.
 *
 * SOLID — Composition over inheritance:
 * This strategy delegates to GPA and Points strategies internally,
 * combining them without duplicating logic.
 *
 * GRASP — High Cohesion:
 * This class focuses purely on combining two eligibility rules.
 */
@Component("combinedEligibilityStrategy")
class CombinedEligibilityStrategy implements EligibilityStrategy {

    // Compose both strategies (composition over inheritance)
    private final EligibilityStrategy gpaStrategy = new GpaEligibilityStrategy();
    private final EligibilityStrategy pointsStrategy = new PointsEligibilityStrategy();

    @Override
    public boolean isEligible(User user, TutorProfile profile) {
        // Both conditions must be true
        return gpaStrategy.isEligible(user, profile)
                && pointsStrategy.isEligible(user, profile);
    }

    @Override
    public String getEligibilityCriteria() {
        return gpaStrategy.getEligibilityCriteria()
                + " AND " + pointsStrategy.getEligibilityCriteria();
    }
}
