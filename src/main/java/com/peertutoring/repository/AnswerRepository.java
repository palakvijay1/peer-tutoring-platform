package com.peertutoring.repository;

import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for Answer.
 *
 * ══════════════════════════════════════════════════════════════
 * CREATIONAL — Singleton Pattern:
 * Spring creates exactly ONE instance of this repository bean
 * and reuses it everywhere (@Repository = singleton by default).
 * This is Spring's implementation of the Singleton pattern.
 *
 * SOLID — Interface Segregation Principle (ISP):
 * Only the methods needed for Answer operations are declared here.
 * We don't pollute this with unrelated queries.
 *
 * GRASP — Low Coupling:
 * We never write SQL manually. Spring generates queries from
 * method names, keeping our code decoupled from database details.
 * ══════════════════════════════════════════════════════════════
 */
@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // Get all answers for a specific question, newest first
    List<Answer> findByQuestionOrderByCreatedAtDesc(Question question);

    // Get all answers submitted by a specific student
    List<Answer> findBySubmittedByOrderByCreatedAtDesc(User user);

    // Get only the verified answer for a question (only one can be verified)
    Optional<Answer> findByQuestionAndVerifiedTrue(Question question);

    // Check if a question already has any answers
    boolean existsByQuestion(Question question);

    // Check if a student already answered this question
    boolean existsByQuestionAndSubmittedBy(Question question, User user);
}