package com.peertutoring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * User entity — represents any platform user (Student, Tutor, Faculty, Admin).
 *
 * Design Principle Applied: Single Responsibility Principle (SRP)
 *   This class is solely responsible for representing user data.
 *   Role-based behaviour is handled separately via the Role enum,
 *   keeping the entity focused and easy to maintain.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;   // stored as BCrypt hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // Points earned by answering questions / tutoring
    @Column(nullable = false)
    @Builder.Default
    private int points = 0;

    public enum Role {
        STUDENT, TUTOR, FACULTY, ADMIN
    }
}