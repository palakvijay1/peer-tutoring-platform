package com.peertutoring.epic4.repository;

import com.peertutoring.epic4.model.SessionRating;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRatingRepository extends JpaRepository<SessionRating, Long> {
    List<SessionRating> findByTutor(User tutor);
    List<SessionRating> findBySession(TutoringSession session);
    boolean existsBySessionAndStudent(TutoringSession session, User student);
}
