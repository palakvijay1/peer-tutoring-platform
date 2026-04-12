package com.peertutoring.service;

import com.peertutoring.dto.Dtos.*;

public interface AuthService {
    AuthResponse signup(SignupRequest request);
    AuthResponse login(LoginRequest request);
}
