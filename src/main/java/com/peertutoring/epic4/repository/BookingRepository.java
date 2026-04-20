package com.peertutoring.epic4.repository;

import com.peertutoring.epic4.model.Booking;
import com.peertutoring.model.TutoringSession;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySession(TutoringSession session);
    Optional<Booking> findBySessionAndStudent(TutoringSession session, User student);
    boolean existsBySessionAndStudent(TutoringSession session, User student);
    long countBySession(TutoringSession session);
}
