package com.peertutoring.epic4.controller;

import com.peertutoring.epic4.dto.Epic4Dtos.*;
import com.peertutoring.epic4.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * MVC
 * Story 2 — Book Session       POST /api/epic4/sessions/{id}/bookings
 * Story 3 — Attend Session  PATCH /api/epic4/sessions/{id}/bookings/attend
 */
@RestController
@RequestMapping("/api/epic4/sessions/{sessionId}/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    /** Story 2: Book a slot */
    @PostMapping
    public ResponseEntity<BookingResponse> bookSession(
            @PathVariable Long sessionId,
            @Valid @RequestBody BookSessionRequest request) {

        return ResponseEntity.ok(bookingService.bookSession(sessionId, request));
    }

    /** List all bookings for a session */
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getBookings(@PathVariable Long sessionId) {
        return ResponseEntity.ok(bookingService.getBookingsForSession(sessionId));
    }

    /** Story 3: Mark attendance */
    @PatchMapping("/attend")
    public ResponseEntity<BookingResponse> markAttendance(
            @PathVariable Long sessionId,
            @Valid @RequestBody MarkAttendanceRequest request) {

        return ResponseEntity.ok(bookingService.markAttendance(sessionId, request));
    }
}
