package com.peertutoring.epic4.controller;

import com.peertutoring.epic4.dto.Epic4Dtos.*;
import com.peertutoring.epic4.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Story 4 — Rate Session
 *
 * POST  /api/epic4/sessions/{id}/ratings   → submit a rating
 * GET   /api/epic4/sessions/{id}/ratings   → list all ratings for session
 */
@RestController
@RequestMapping("/api/epic4/sessions/{sessionId}/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingResponse> submitRating(
            @PathVariable Long sessionId,
            @Valid @RequestBody SubmitRatingRequest request) {

        return ResponseEntity.ok(ratingService.submitRating(sessionId, request));
    }

    @GetMapping
    public ResponseEntity<List<RatingResponse>> getRatings(@PathVariable Long sessionId) {
        return ResponseEntity.ok(ratingService.getRatingsForSession(sessionId));
    }
}
