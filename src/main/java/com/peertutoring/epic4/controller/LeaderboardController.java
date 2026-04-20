package com.peertutoring.epic4.controller;

import com.peertutoring.epic4.dto.Epic4Dtos.LeaderboardResponse;
import com.peertutoring.epic4.service.LeaderboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Story 5 — Leaderboard
 *
 * GET /api/epic4/leaderboard  → ranked list of tutors by average rating
 *
 * No write endpoint here — the leaderboard is updated automatically
 * by the Observer (LeaderboardUpdater) whenever a rating is submitted.
 */
@RestController
@RequestMapping("/api/epic4/leaderboard")
@RequiredArgsConstructor
public class LeaderboardController {

    private final LeaderboardService leaderboardService;

    @GetMapping
    public ResponseEntity<List<LeaderboardResponse>> getLeaderboard() {
        return ResponseEntity.ok(leaderboardService.getLeaderboard());
    }
}
