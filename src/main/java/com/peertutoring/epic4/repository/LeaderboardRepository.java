package com.peertutoring.epic4.repository;

import com.peertutoring.epic4.model.LeaderboardEntry;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeaderboardRepository extends JpaRepository<LeaderboardEntry, Long> {
    Optional<LeaderboardEntry> findByTutor(User tutor);
    List<LeaderboardEntry> findAllByOrderByAverageRatingDescTotalRatingsDesc();
}
