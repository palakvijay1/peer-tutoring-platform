package com.peertutoring.pattern.decorator;

import com.peertutoring.model.User;

// ── ConcreteComponent ─────────────────────────────────────────────────────────
class BasicUserProfile implements UserProfileComponent {
    private final User user;

    public BasicUserProfile(User user) {
        this.user = user;
    }

    @Override
    public String getDisplayName() {
        return user.getName();
    }

    @Override
    public String getSummary() {
        return "User: " + user.getName() + " | Email: " + user.getEmail();
    }
}

// ── Base Decorator ────────────────────────────────────────────────────────────
abstract class UserProfileDecorator implements UserProfileComponent {
    protected final UserProfileComponent wrapped;

    protected UserProfileDecorator(UserProfileComponent wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName();
    }

    @Override
    public String getSummary() {
        return wrapped.getSummary();
    }
}

// ── ConcreteDecorator 1: Adds points badge ────────────────────────────────────
class PointsBadgeDecorator extends UserProfileDecorator {
    private final int points;

    public PointsBadgeDecorator(UserProfileComponent wrapped, int points) {
        super(wrapped);
        this.points = points;
    }

    @Override
    public String getDisplayName() {
        return wrapped.getDisplayName() + " [⭐ " + points + " pts]";
    }

    @Override
    public String getSummary() {
        return wrapped.getSummary() + " | Points: " + points;
    }
}

// ── ConcreteDecorator 2: Adds role label ─────────────────────────────────────
class RoleLabelDecorator extends UserProfileDecorator {
    private final String role;

    public RoleLabelDecorator(UserProfileComponent wrapped, String role) {
        super(wrapped);
        this.role = role;
    }

    @Override
    public String getDisplayName() {
        return "[" + role + "] " + wrapped.getDisplayName();
    }

    @Override
    public String getSummary() {
        return wrapped.getSummary() + " | Role: " + role;
    }
}

/**
 * Factory method to compose decorators conveniently.
 */
public class UserProfileBuilder {

    public static UserProfileComponent buildEnrichedProfile(User user) {
        UserProfileComponent profile = new BasicUserProfile(user);
        profile = new PointsBadgeDecorator(profile, user.getPoints());
        profile = new RoleLabelDecorator(profile, user.getRole().name());
        return profile;
    }

    public static UserProfileComponent buildBasicProfile(User user) {
        return new BasicUserProfile(user);
    }
}