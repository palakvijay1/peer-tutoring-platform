package com.peertutoring.pattern.factory;

import com.peertutoring.dto.Dtos.SignupRequest;
import com.peertutoring.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {

    public User createStudent(SignupRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .role(User.Role.STUDENT)
                .active(true)
                .points(0)
                .build();
    }

    public User createFaculty(SignupRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .role(User.Role.FACULTY)
                .active(true)
                .points(0)
                .build();
    }

    public User createTutor(SignupRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase().trim())
                .password(request.getPassword())
                .role(User.Role.TUTOR)
                .active(true)
                .points(0)
                .build();
    }
}