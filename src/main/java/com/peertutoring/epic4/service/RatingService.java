package com.peertutoring.epic4.service;

import com.peertutoring.epic4.dto.Epic4Dtos.*;
import com.peertutoring.epic4.model.SessionRating;
import com.peertutoring.epic4.pattern.observer.RatingEventObserver;
import com.peertutoring.epic4.repository.BookingRepository;
import com.peertutoring.epic4.repository.SessionRatingRepository;
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
public class RatingService {

    private final SessionRatingRepository ratingRepository;
    private final BookingRepository bookingRepository;
    private final TutoringSessionRepository sessionRepository;
    private final UserRepository userRepository;

    // Observer Pattern: Spring injects ALL beans implementing RatingEventObserver
    // (currently LeaderboardUpdater; adding a new observer = new @Component bean only)
    private final List<RatingEventObserver> observers;

    /** Story 4: Submit a rating for a completed session. */
    @Transactional
    public RatingResponse submitRating(Long sessionId, SubmitRatingRequest request) {
        TutoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found: " + request.getStudentId()));

        // Prevent a tutor from rating their own session
        if (session.getTutor().getId().equals(student.getId())) {
            throw new RuntimeException("Tutors cannot rate their own sessions.");
        }

        // Student must have a booking and must have attended
        var booking = bookingRepository.findBySessionAndStudent(session, student)
                .orElseThrow(() -> new RuntimeException("You must book the session before rating."));

        if (!booking.isAttended()) {
            throw new RuntimeException("You must attend the session before rating.");
        }

        if (ratingRepository.existsBySessionAndStudent(session, student)) {
            throw new RuntimeException("You have already rated this session.");
        }

        SessionRating rating = SessionRating.builder()
                .session(session)
                .student(student)
                .tutor(session.getTutor())
                .score(request.getScore())
                .feedback(request.getFeedback())
                .build();

        ratingRepository.save(rating);

        // Observer Pattern: notify all observers — leaderboard updates automatically
        observers.forEach(o -> o.onRatingSubmitted(session.getTutor()));

        return RatingResponse.from(rating, "Rating submitted. Leaderboard updated.");
    }

    public List<RatingResponse> getRatingsForSession(Long sessionId) {
        TutoringSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        return ratingRepository.findBySession(session).stream()
                .map(r -> RatingResponse.from(r, null))
                .collect(Collectors.toList());
    }
}
