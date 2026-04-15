package com.peertutoring.service.impl;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.DuplicateResourceException;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.User;
import com.peertutoring.pattern.factory.UserFactory;
import com.peertutoring.repository.UserRepository;
import com.peertutoring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ══════════════════════════════════════════════════════════════
 * SOLID — Single Responsibility Principle (SRP):
 * Only handles authentication — signup and login.
 *
 * CREATIONAL — Factory Pattern:
 * Uses UserFactory to create User objects by role.
 * This keeps object creation logic OUT of the service.
 *
 * CREATIONAL — Singleton:
 * Spring creates one instance of this service bean.
 * ══════════════════════════════════════════════════════════════
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserFactory userFactory; // Factory Pattern

    @Override
    public AuthResponse signup(SignupRequest request) {

        // Check for duplicate email
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new DuplicateResourceException(
                    "An account with email " + request.getEmail() + " already exists.");
        }

        // Factory Pattern: delegate User creation to UserFactory
        // UserFactory decides which type of User to create based on role
        // This follows SRP — AuthServiceImpl doesn't know HOW to build a User
        User user;
        String role = request.getRole();

        if ("FACULTY".equalsIgnoreCase(role)) {
            user = userFactory.createFaculty(request);
        } else {
            // Default to STUDENT if no role specified
            user = userFactory.createStudent(request);
        }

        // Hash would go here in production — keeping plain for demo
        user.setPassword(request.getPassword());

        @SuppressWarnings("null")
        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .token("demo-token-" + savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .role(savedUser.getRole().name())
                .message("Account created successfully!")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid email or password"));

        if (!request.getPassword().equals(user.getPassword())) {
            throw new ResourceNotFoundException("Invalid email or password");
        }

    return AuthResponse.builder()
        .token("demo-token-" + user.getId())
        .email(user.getEmail())
        .name(user.getName())
        .role(user.getRole().name())
        .points(user.getPoints())   // ← ADD THIS LINE
        .message("Login successful!")
        .build();
    }
}