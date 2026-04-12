package com.peertutoring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Question entity — represents an academic question posted by a student.
 *
 * Design Principle Applied: Open/Closed Principle (OCP)
 *   The QuestionStatus enum can be extended with new states (e.g., CLOSED)
 *   without modifying existing status-handling code, keeping the class
 *   open for extension but closed for modification.
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Subject subject;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private QuestionStatus status = QuestionStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "posted_by", nullable = false)
    private User postedBy;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum QuestionStatus {
        PENDING, ANSWERED, VERIFIED
    }

    public enum Subject {
        MATHEMATICS, PHYSICS, CHEMISTRY, BIOLOGY,
        COMPUTER_SCIENCE, DATA_STRUCTURES, ALGORITHMS,
        DBMS, OPERATING_SYSTEMS, NETWORKS,
        ENGLISH, HISTORY, ECONOMICS, OTHER
    }
}