package com.peertutoring.epic4.service;

import com.peertutoring.epic4.dto.Epic4Dtos.LeaderboardResponse;
import com.peertutoring.epic4.repository.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaderboardService {

    private final LeaderboardRepository leaderboardRepository;

    /**
     * Story 5: Returns tutors ranked by average rating (descending).
     * The leaderboard is already kept up-to-date by LeaderboardUpdater
     * (Observer) — this method just reads and returns the current state.
     */
    public List<LeaderboardResponse> getLeaderboard() {
        return leaderboardRepository
                .findAllByOrderByAverageRatingDescTotalRatingsDesc()
                .stream()
                .map(LeaderboardResponse::from)
                .collect(Collectors.toList());
    }
}
