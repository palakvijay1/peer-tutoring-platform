package com.peertutoring.pattern.decorator;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Decorator (Structural)
 * ══════════════════════════════════════════════════════════════
 *
 * WHY: We need to display a user's profile in enriched ways depending on
 *      context — a basic view vs. a view with points badge vs. a view with
 *      role-specific labels — without changing the User entity.
 *      The Decorator pattern wraps the base component and adds behaviour.
 *
 * HOW IT APPLIES HERE:
 *   - UserProfileComponent is the Component interface.
 *   - BasicUserProfile is the ConcreteComponent.
 *   - PointsBadgeDecorator wraps it to add points display.
 *   - RoleLabelDecorator wraps it to add a role badge string.
 *
 * HOW TO SHOW IN REPORT:
 *   "The Decorator pattern lets us compose user profile representations
 *    at runtime. Controllers can wrap profiles with as many decorators as
 *    needed, avoiding a combinatorial explosion of subclasses."
 */
public interface UserProfileComponent {
    String getDisplayName();
    String getSummary();
}