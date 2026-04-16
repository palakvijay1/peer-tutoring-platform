package com.peertutoring.repository;

import com.peertutoring.model.Question;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for TutoringSession.
 *
 * SOLID — Interface Segregation Principle (ISP):
 *   Only session-related queries live here.
 *
 * GRASP — Low Coupling:
 *   No manual SQL — Spring generates from method names.
 */
@Repository
public interface TutoringSessionRepository extends JpaRepository<TutoringSession, Long> {

    // Get all sessions created by a specific tutor, newest first
    List<TutoringSession> findByTutorOrderByScheduledAtDesc(User tutor);

    // Get all sessions by subject
    List<TutoringSession> findBySubjectOrderByScheduledAtAsc(Question.Subject subject);

    // Get all upcoming sessions (status = UPCOMING), newest first
    List<TutoringSession> findByStatusOrderByScheduledAtAsc(TutoringSession.SessionStatus status);

    // Get all sessions ordered by scheduled date
    List<TutoringSession> findAllByOrderByScheduledAtAsc();
}
