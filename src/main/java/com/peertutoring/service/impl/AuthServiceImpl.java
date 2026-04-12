package com.peertutoring.service.impl;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.DuplicateResourceException;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.User;
import com.peertutoring.repository.UserRepository;
import com.peertutoring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    @Override
    public AuthResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new DuplicateResourceException(
                "An account with email " + request.getEmail() + " already exists.");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .role(User.Role.STUDENT)
                .active(true)
                .points(0)
                .build();

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
                .message("Login successful!")
                .build();
    }
}