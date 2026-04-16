package com.peertutoring.pattern.strategy;

import com.peertutoring.model.TutorProfile;
import com.peertutoring.model.User;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Strategy (Behavioral) — EPIC 3
 * ══════════════════════════════════════════════════════════════
 *
 * WHY this pattern is chosen:
 *   Eligibility rules can vary — a student might qualify by GPA,
 *   by points earned, by rating, or by a combination of criteria.
 *   Hard-coding all rules in TutorService violates SRP and OCP.
 *   The Strategy pattern lets us encapsulate each rule separately
 *   and swap them without touching the service.
 *
 * HOW it is applied:
 *   - EligibilityStrategy is the Strategy interface.
 *   - GpaEligibilityStrategy checks GPA >= 3.0
 *   - PointsEligibilityStrategy checks points >= 50
 *   - RatingEligibilityStrategy checks rating >= 4.0
 *   - CombinedEligibilityStrategy checks all criteria together
 *
 * BENEFITS:
 *   1. Open/Closed Principle — new criteria = new class, no changes to existing ones
 *   2. Single Responsibility — each strategy has one job
 *   3. Easy to test each rule independently
 *   4. Easy to explain in viva: "I can add new eligibility rules without touching service code"
 *
 * WHERE it is used:
 *   TutorServiceImpl injects the desired EligibilityStrategy bean.
 *   When checkEligibility() is called, it delegates to the strategy.
 * ══════════════════════════════════════════════════════════════
 */
public interface EligibilityStrategy {

    /**
     * Check if a user is eligible to become a tutor.
     *
     * @param user    the user applying to become a tutor
     * @param profile their TutorProfile containing GPA, rating
     * @return true if eligible, false otherwise
     */
    boolean isEligible(User user, TutorProfile profile);

    /**
     * Returns a human-readable reason if not eligible.
     * Helps provide clear feedback to the user.
     */
    String getEligibilityCriteria();
}
