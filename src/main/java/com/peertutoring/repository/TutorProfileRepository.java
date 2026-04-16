package com.peertutoring.repository;

import com.peertutoring.model.TutorProfile;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for TutorProfile.
 *
 * SOLID — Dependency Inversion Principle (DIP):
 *   Services depend on this interface (abstraction),
 *   not on any concrete DB implementation.
 *
 * GRASP — Low Coupling:
 *   Spring auto-generates the SQL queries from method names.
 *   Our code never touches raw SQL.
 */
@Repository
public interface TutorProfileRepository extends JpaRepository<TutorProfile, Long> {

    // Find the tutor profile linked to a user
    Optional<TutorProfile> findByUser(User user);

    // Find tutor profile by user's email
    Optional<TutorProfile> findByUserEmail(String email);

    // Check if a user already has a tutor profile (to prevent duplicates)
    boolean existsByUser(User user);

    // Get all approved tutors
    List<TutorProfile> findByApprovedTrue();
}
