package com.peertutoring.service;

import com.peertutoring.dto.Dtos.*;

import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * TutorService Interface — EPIC 3
 *
 * SOLID — Dependency Inversion Principle (DIP):
 * TutorController depends on THIS interface (abstraction),
 * NOT on TutorServiceImpl (concrete class).
 * This means the implementation can be swapped without touching
 * the controller at all.
 *
 * SOLID — Interface Segregation Principle (ISP):
 * Only tutor-related methods live here.
 * Question methods → QuestionService
 * Answer methods → AnswerService
 * Auth methods → AuthService
 *
 * GRASP — Controller (indirectly):
 * TutorController is the GRASP controller for EPIC 3 events.
 * It delegates all logic to this service.
 * ══════════════════════════════════════════════════════════════
 */
public interface TutorService {

    /**
     * Story 1: Register as Tutor
     * A STUDENT registers to become a TUTOR.
     * Creates a TutorProfile with their GPA and bio.
     * Changes their User role to TUTOR.
     *
     * @param request contains GPA and bio
     * @param email   the student's email (from request header)
     */
    TutorProfileResponse registerAsTutor(RegisterAsTutorRequest request, String email);

    /**
     * Story 2: Check Eligibility
     * Check if a user meets the criteria to become/remain a tutor.
     * Uses Strategy Pattern — criteria can be GPA, points, rating, or combined.
     *
     * @param email the user's email
     */
    EligibilityResponse checkEligibility(String email);

    /**
     * Story 3 + 4: Create Session with Details
     * Tutor creates a new tutoring session with full details:
     * topic, subject, scheduled time, capacity, description.
     *
     * Uses Factory Pattern internally to construct the session.
     *
     * @param request session details
     * @param email   the tutor's email
     */
    SessionResponse createSession(CreateSessionRequest request, String email);

    /**
     * Story 4b: Update Session Details
     * Tutor can update the topic, time, capacity, or description
     * of a session they created.
     *
     * @param sessionId the session to update
     * @param request   updated details
     * @param email     tutor's email (ownership check)
     */
    SessionResponse updateSession(Long sessionId, CreateSessionRequest request, String email);

    /**
     * Story 5: View Created Sessions
     * Tutor views all sessions they have created.
     *
     * @param email the tutor's email
     */
    List<SessionResponse> getMySessions(String email);

    /**
     * View all sessions (for students browsing available sessions).
     */
    List<SessionResponse> getAllSessions();

    /**
     * Get a single session by ID.
     */
    SessionResponse getSessionById(Long id);
}
