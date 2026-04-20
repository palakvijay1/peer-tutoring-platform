package com.peertutoring.epic4.controller;

import com.peertutoring.epic4.dto.Epic4Dtos.SessionSummaryResponse;
import com.peertutoring.epic4.service.SessionBrowseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Story 1 — Browse Sessions.
 *
 * GET /api/epic4/sessions
 *   → all upcoming sessions
 *
 * GET /api/epic4/sessions?filterType=subjectFilter&filterValue=MATH
 *   → sessions filtered by subject
 *
 * GET /api/epic4/sessions?filterType=tutorFilter&filterValue=Alice
 *   → sessions filtered by tutor name
 */
@RestController
@RequestMapping("/api/epic4/sessions")
@RequiredArgsConstructor
public class SessionBrowseController {

    private final SessionBrowseService sessionBrowseService;

    @GetMapping
    public ResponseEntity<List<SessionSummaryResponse>> browseSessions(
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterValue) {

        return ResponseEntity.ok(sessionBrowseService.browseSessions(filterType, filterValue));
    }
}
