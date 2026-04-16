package com.peertutoring.controller;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.service.TutorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * TutorController — EPIC 3 REST API Controller
 * ══════════════════════════════════════════════════════════════
 *
 * GRASP — Controller Pattern:
 *   This class is the GRASP Controller for EPIC 3.
 *   It receives all HTTP system events for tutor/session operations
 *   and delegates 100% of business logic to TutorService.
 *   Zero business logic lives here.
 *
 * SOLID — Single Responsibility Principle (SRP):
 *   This class only handles HTTP concerns: parse request,
 *   call service, wrap response. Nothing else.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 *   Depends on TutorService (interface), not TutorServiceImpl (concrete).
 *   The implementation can be swapped without touching this controller.
 *
 * CREATIONAL — Singleton Pattern:
 *   @RestController makes Spring create ONE instance of this controller
 *   and reuse it for all requests. Same as teammates' controllers.
 *
 * API ENDPOINTS:
 *   POST   /api/tutor/register           → Story 1: Register as Tutor
 *   GET    /api/tutor/eligibility        → Story 2: Check Eligibility
 *   POST   /api/tutor/sessions           → Story 3+4: Create Session
 *   PUT    /api/tutor/sessions/{id}      → Story 4b: Update Session
 *   GET    /api/tutor/sessions/my        → Story 5: View My Sessions
 *   GET    /api/tutor/sessions           → View All Sessions (students)
 *   GET    /api/tutor/sessions/{id}      → Get Session by ID
 * ══════════════════════════════════════════════════════════════
 */
@RestController
@RequestMapping("/api/tutor")
@RequiredArgsConstructor
public class TutorController {

    // DIP: depends on interface, not implementation
    private final TutorService tutorService;

    // ─────────────────────────────────────────────────────────────────────────
    // STORY 1: Register as Tutor
    // POST /api/tutor/register
    // Header: X-User-Email: student@example.com
    // Body: { "gpa": 3.5, "bio": "Strong in Data Structures" }
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TutorProfileResponse>> registerAsTutor(
            @Valid @RequestBody RegisterAsTutorRequest request,
            @RequestHeader("X-User-Email") String email) {

        TutorProfileResponse response = tutorService.registerAsTutor(request, email);
        return ResponseEntity.ok(
                ApiResponse.ok("Tutor registered successfully!", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STORY 2: Check Eligibility
    // GET /api/tutor/eligibility
    // Header: X-User-Email: student@example.com
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/eligibility")
    public ResponseEntity<ApiResponse<EligibilityResponse>> checkEligibility(
            @RequestHeader("X-User-Email") String email) {

        EligibilityResponse response = tutorService.checkEligibility(email);
        return ResponseEntity.ok(
                ApiResponse.ok("Eligibility check complete.", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STORY 3 + 4: Create Session with Details
    // POST /api/tutor/sessions
    // Header: X-User-Email: tutor@example.com
    // Body: { "topic": "...", "subject": "MATHEMATICS", "scheduledAt": "...",
    //         "capacity": 10, "description": "..." }
    // ─────────────────────────────────────────────────────────────────────────
    @PostMapping("/sessions")
    public ResponseEntity<ApiResponse<SessionResponse>> createSession(
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-User-Email") String email) {

        SessionResponse response = tutorService.createSession(request, email);
        return ResponseEntity.ok(
                ApiResponse.ok("Session created successfully!", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STORY 4b: Update Session Details
    // PUT /api/tutor/sessions/{id}
    // Header: X-User-Email: tutor@example.com
    // Body: (same as CreateSessionRequest with updated values)
    // ─────────────────────────────────────────────────────────────────────────
    @PutMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> updateSession(
            @PathVariable Long id,
            @Valid @RequestBody CreateSessionRequest request,
            @RequestHeader("X-User-Email") String email) {

        SessionResponse response = tutorService.updateSession(id, request, email);
        return ResponseEntity.ok(
                ApiResponse.ok("Session updated successfully!", response));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // STORY 5: View My Sessions (Tutor sees only their own sessions)
    // GET /api/tutor/sessions/my
    // Header: X-User-Email: tutor@example.com
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/sessions/my")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getMySessions(
            @RequestHeader("X-User-Email") String email) {

        List<SessionResponse> sessions = tutorService.getMySessions(email);
        return ResponseEntity.ok(
                ApiResponse.ok("Your sessions fetched!", sessions));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // View ALL Sessions (students browse available sessions)
    // GET /api/tutor/sessions
    // No header required — public browsing
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse<List<SessionResponse>>> getAllSessions() {

        List<SessionResponse> sessions = tutorService.getAllSessions();
        return ResponseEntity.ok(
                ApiResponse.ok("All sessions fetched!", sessions));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Get Session By ID
    // GET /api/tutor/sessions/{id}
    // ─────────────────────────────────────────────────────────────────────────
    @GetMapping("/sessions/{id}")
    public ResponseEntity<ApiResponse<SessionResponse>> getSessionById(
            @PathVariable Long id) {

        SessionResponse response = tutorService.getSessionById(id);
        return ResponseEntity.ok(
                ApiResponse.ok("Session found!", response));
    }
}
