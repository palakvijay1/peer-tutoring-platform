package com.peertutoring.pattern.strategy;

import com.peertutoring.model.Question;
import java.util.List;

/**
 * ══════════════════════════════════════════════════════════════
 * DESIGN PATTERN: Strategy (Behavioral)
 * ══════════════════════════════════════════════════════════════
 *
 * WHY: Questions can be sorted / filtered in multiple ways — by date, by
 *      subject, by status. Embedding all these algorithms inside QuestionService
 *      violates SRP and OCP. The Strategy pattern extracts each sorting
 *      algorithm into its own class, letting the caller inject the desired one.
 *
 * HOW IT APPLIES HERE:
 *   - QuestionSortStrategy is the Strategy interface.
 *   - SortByNewest, SortBySubject, SortByStatus are ConcreteStrategies.
 *   - QuestionService accepts a strategy and delegates sorting to it.
 *
 * HOW TO SHOW IN REPORT:
 *   "The Strategy pattern allows question sorting algorithms to vary
 *    independently from the service that uses them. Adding a new sort
 *    order (e.g., by popularity) requires only a new Strategy class."
 */
public interface QuestionSortStrategy {
    List<Question> sort(List<Question> questions);
}