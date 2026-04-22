package com.peertutoring.epic4.model;

import com.peertutoring.model.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * ══════════════════════════════════════════════════════════════
 * LeaderboardEntry Entity — EPIC 4 (Story 5)
 *
 * Stores the computed ranking for each tutor.
 * Automatically updated by the LeaderboardObserver whenever
 * a new SessionRating is submitted.
 * 
 * OBSERVER PATTERN(BEHAVIORAL) UPDATES THIS
 * THIS IS NOT THE OBSERVER ITSELF THIS CLASS IS THE DATA MODEL UPDATED BY
 * THE OBSERVER
 * 
 * SRP ONLY STORES LEADERBOARD DATA DOES NOT CALC RATINGS ETC
 * CLEAR SEPERATION OF RESPONSIBILITY
 *
 * One row per tutor — upserted (not inserted) on every rating event.
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "epic4_leaderboard")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderboardEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id", unique = true, nullable = false)
    private User tutor;

    @Column(nullable = false)
    @Builder.Default
    private double averageRating = 0.0;

    @Column(nullable = false)
    @Builder.Default
    private int totalRatings = 0;

    /** Rank is recomputed after every rating submission. */
    @Column(nullable = false)
    @Builder.Default
    private int rank = 0;
}
