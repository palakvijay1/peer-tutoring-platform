package com.peertutoring.epic4.service;

import com.peertutoring.epic4.dto.Epic4Dtos.*;
import com.peertutoring.epic4.model.Booking;
import com.peertutoring.epic4.pattern.factory.BookingFactory;
import com.peertutoring.epic4.repository.BookingRepository;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import com.peertutoring.repository.TutoringSessionRepository;
import com.peertutoring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TutoringSessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final BookingFactory bookingFactory;   // Factory Pattern

    /** Story 2: Book a session slot. */
    @Transactional
    public BookingResponse bookSession(Long sessionId, BookSessionRequest request) {
        TutoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        if (session.getStatus() != TutoringSession.SessionStatus.UPCOMING) {
            throw new RuntimeException("Cannot book a session that is not UPCOMING.");
        }

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        // Prevent a tutor from booking their own session
        if (session.getTutor().getId().equals(student.getId())) {
            throw new RuntimeException("Tutors cannot book their own sessions.");
        }

        if (bookingRepository.existsBySessionAndStudent(session, student)) {
            throw new RuntimeException("You have already booked this session.");
        }

        long currentBookings = bookingRepository.countBySession(session);
        if (currentBookings >= session.getCapacity()) {
            throw new RuntimeException("Session is full (capacity: " + session.getCapacity() + ").");
        }

        // Factory Pattern: construction delegated to BookingFactory
        Booking booking = bookingFactory.createBooking(session, student);
        bookingRepository.save(booking);

        return BookingResponse.from(booking, "Session booked successfully.");
    }

    /** Story 3: Mark a student as having attended the session. */
    @Transactional
    public BookingResponse markAttendance(Long sessionId, MarkAttendanceRequest request) {
        TutoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        Booking booking = bookingRepository.findBySessionAndStudent(session, student)
                .orElseThrow(() -> new RuntimeException("No booking found for this student in this session."));

        booking.setAttended(true);
        bookingRepository.save(booking);

        return BookingResponse.from(booking, "Attendance marked successfully.");
    }

    /** Get all bookings for a session (used by tutor to see who booked). */
    public List<BookingResponse> getBookingsForSession(Long sessionId) {
        TutoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        return bookingRepository.findBySession(session).stream()
                .map(b -> BookingResponse.from(b, null))
                .collect(Collectors.toList());
    }
}
