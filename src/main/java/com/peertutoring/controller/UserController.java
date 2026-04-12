package com.peertutoring.controller;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.User;
import com.peertutoring.pattern.decorator.UserProfileBuilder;
import com.peertutoring.pattern.decorator.UserProfileComponent;
import com.peertutoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * MVC — Controller Layer
 * Demonstrates Decorator Pattern: profile can be basic or enriched.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    // Get current user's basic profile
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return ResponseEntity.ok(ApiResponse.ok("Profile fetched!", UserResponse.from(user)));
    }

    // Get enriched profile summary using Decorator Pattern
    @GetMapping("/me/summary")
    public ResponseEntity<ApiResponse<Map<String, String>>> getEnrichedProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Decorator Pattern: wraps basic profile with points badge + role label
        UserProfileComponent enriched = UserProfileBuilder.buildEnrichedProfile(user);

        Map<String, String> summary = Map.of(
                "displayName", enriched.getDisplayName(),
                "summary", enriched.getSummary()
        );

        return ResponseEntity.ok(ApiResponse.ok("Enriched profile!", summary));
    }
}
