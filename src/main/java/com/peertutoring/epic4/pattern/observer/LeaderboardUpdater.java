package com.peertutoring.epic4.pattern.observer;

import com.peertutoring.epic4.model.LeaderboardEntry;
import com.peertutoring.epic4.model.SessionRating;
import com.peertutoring.epic4.repository.LeaderboardRepository;
import com.peertutoring.epic4.repository.SessionRatingRepository;
import com.peertutoring.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Concrete Observer — recomputes leaderboard whenever any tutor
 * receives a new rating. Registered automatically by Spring as a
 * RatingEventObserver bean — RatingService never imports this class.
 */
@Component
@RequiredArgsConstructor
public class LeaderboardUpdater implements RatingEventObserver {

    private final SessionRatingRepository ratingRepository;
    private final LeaderboardRepository leaderboardRepository;

    @Override
    public void onRatingSubmitted(User tutor) {
        List<SessionRating> ratings = ratingRepository.findByTutor(tutor);
        if (ratings.isEmpty()) return;

        double avg = ratings.stream()
                .mapToInt(SessionRating::getScore)
                .average()
                .orElse(0.0);

        // Upsert — create entry if tutor has no leaderboard row yet
        LeaderboardEntry entry = leaderboardRepository
                .findByTutor(tutor)
                .orElse(LeaderboardEntry.builder().tutor(tutor).build());

        entry.setAverageRating(Math.round(avg * 100.0) / 100.0);
        entry.setTotalRatings(ratings.size());
        leaderboardRepository.save(entry);

        // Recompute ranks for all tutors
        List<LeaderboardEntry> all =
                leaderboardRepository.findAllByOrderByAverageRatingDescTotalRatingsDesc();
        for (int i = 0; i < all.size(); i++) {
            all.get(i).setRank(i + 1);
        }
        leaderboardRepository.saveAll(all);
    }
}
