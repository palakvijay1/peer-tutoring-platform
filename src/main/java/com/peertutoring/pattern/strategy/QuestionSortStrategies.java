package com.peertutoring.pattern.strategy;

import com.peertutoring.model.Question;
import org.springframework.stereotype.Component;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// ── ConcreteStrategy 1: Sort by newest first ──────────────────────────────────
@Component("sortByNewest")
class SortByNewest implements QuestionSortStrategy {
    @Override
    public List<Question> sort(List<Question> questions) {
        return questions.stream()
                .sorted(Comparator.comparing(Question::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }
}

// ── ConcreteStrategy 2: Sort by subject (alphabetical) ───────────────────────
@Component("sortBySubject")
class SortBySubject implements QuestionSortStrategy {
    @Override
    public List<Question> sort(List<Question> questions) {
        return questions.stream()
                .sorted(Comparator.comparing(q -> q.getSubject().name()))
                .collect(Collectors.toList());
    }
}

// ── ConcreteStrategy 3: Sort by status (PENDING first) ───────────────────────
@Component("sortByStatus")
class SortByStatus implements QuestionSortStrategy {

    // PENDING < ANSWERED < VERIFIED
    private static final java.util.Map<Question.QuestionStatus, Integer> ORDER =
            java.util.Map.of(
                    Question.QuestionStatus.PENDING, 0,
                    Question.QuestionStatus.ANSWERED, 1,
                    Question.QuestionStatus.VERIFIED, 2
            );

    @Override
    public List<Question> sort(List<Question> questions) {
        return questions.stream()
                .sorted(Comparator.comparingInt(q -> ORDER.getOrDefault(q.getStatus(), 99)))
                .collect(Collectors.toList());
    }
}