package com.peertutoring.repository;

import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA repository for Question.
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByPostedBy(User user);
    List<Question> findBySubject(Question.Subject subject);
    List<Question> findByStatus(Question.QuestionStatus status);
    List<Question> findByPostedByOrderByCreatedAtDesc(User user);
    List<Question> findAllByOrderByCreatedAtDesc();
}