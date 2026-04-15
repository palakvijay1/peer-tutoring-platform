package com.peertutoring.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Answer entity — represents an answer submitted by a student for a question.
 *
 * ══════════════════════════════════════════════════════════════
 * SOLID — Single Responsibility Principle (SRP):
 * This class only holds answer data. No business logic here.
 * Verification, status transitions, point rewards all live
 * in the service layer.
 *
 * SOLID — Open/Closed Principle (OCP):
 * AnswerStatus enum can be extended (e.g. DISPUTED) without
 * touching any existing code.
 *
 * CREATIONAL — Builder Pattern (via Lombok @Builder):
 * Allows fluent construction:
 *   Answer.builder().text(...).question(...).submittedBy(...).build()
 * Avoids telescoping constructors, keeps creation readable.
 * Same approach your teammate used for Question.java and User.java.
 * ══════════════════════════════════════════════════════════════
 */
@Entity
@Table(name = "answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // The answer text submitted by the student
    @Column(nullable = false, length = 2000)
    private String text;

    // Which question this answer belongs to
    // Many answers can belong to one question
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    // Which student submitted this answer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", nullable = false)
    private User submittedBy;

    // Whether this answer has been verified by faculty
    // Default is false — only faculty can set this to true
    @Column(nullable = false)
    @Builder.Default
    private boolean verified = false;

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}