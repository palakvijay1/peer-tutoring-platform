package com.peertutoring.pattern.factory;

import com.peertutoring.model.Answer;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import org.springframework.stereotype.Component;

/**
 * ══════════════════════════════════════════════════════════════
 * CREATIONAL PATTERN: Factory
 * ══════════════════════════════════════════════════════════════
 *
 * WHY: Answer creation involves setting multiple fields correctly
 *      (text, question link, submitter, verified=false by default).
 *      Instead of repeating this setup everywhere, we centralise
 *      it here. If creation logic changes (e.g. add a new field),
 *      we change it in ONE place only.
 *
 * HOW IT RELATES TO UserFactory (your teammate's work):
 *   UserFactory creates User objects of different roles.
 *   AnswerFactory creates Answer objects for different contexts
 *   (student answer vs faculty-created sample answer).
 *
 * SOLID — SRP: This class has one job — create Answer objects.
 *
 * CREATIONAL — Singleton: Spring manages one instance of this
 *   factory (@Component = singleton bean). The same factory
 *   object is reused across all service calls.
 * ══════════════════════════════════════════════════════════════
 */
@Component
public class AnswerFactory {

    /**
     * Creates a standard student answer.
     * verified is false by default — faculty must verify it.
     *
     * @param text        the answer content written by the student
     * @param question    the question this answer belongs to
     * @param submittedBy the student submitting the answer
     */
    public Answer createStudentAnswer(String text, Question question, User submittedBy) {
        // Builder pattern used here — clean, readable, no constructor chaos
        return Answer.builder()
                .text(text)
                .question(question)
                .submittedBy(submittedBy)
                .verified(false)   // Always starts unverified
                .build();
    }
}