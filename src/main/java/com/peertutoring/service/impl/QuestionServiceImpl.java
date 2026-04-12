package com.peertutoring.service.impl;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.exception.ResourceNotFoundException;
import com.peertutoring.model.Question;
import com.peertutoring.model.User;
import com.peertutoring.pattern.observer.*;
import com.peertutoring.pattern.strategy.QuestionSortStrategy;
import com.peertutoring.repository.QuestionRepository;
import com.peertutoring.repository.UserRepository;
import com.peertutoring.service.QuestionService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Design Patterns used here:
 *   - Observer: publishes question events to listeners (notification, audit)
 *   - Strategy: delegates sorting algorithm based on caller's choice
 *
 * Design Principle:
 *   - Open/Closed: new sort strategies or event listeners can be added
 *     without touching this class.
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final QuestionEventPublisher eventPublisher;
    private final NotificationListener notificationListener;
    private final AuditListener auditListener;
    private final Map<String, QuestionSortStrategy> sortStrategies;

    public QuestionServiceImpl(
            QuestionRepository questionRepository,
            UserRepository userRepository,
            QuestionEventPublisher eventPublisher,
            NotificationListener notificationListener,
            AuditListener auditListener,
            @Qualifier("sortByNewest") QuestionSortStrategy sortByNewest,
            @Qualifier("sortBySubject") QuestionSortStrategy sortBySubject,
            @Qualifier("sortByStatus") QuestionSortStrategy sortByStatus) {

        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.notificationListener = notificationListener;
        this.auditListener = auditListener;
        this.sortStrategies = Map.of(
                "newest", sortByNewest,
                "subject", sortBySubject,
                "status", sortByStatus
        );
    }

    // Register observers once bean is ready
    @PostConstruct
    public void registerObservers() {
        eventPublisher.subscribe(notificationListener);
        eventPublisher.subscribe(auditListener);
    }

    @SuppressWarnings("null")
    @Override
    public QuestionResponse postQuestion(PostQuestionRequest request, String email) {
        User user = getUser(email);

        // Story 3 AC: build question with PENDING status (default in model)
        Question question = Question.builder()
                .text(request.getText())
                .subject(request.getSubject())
                .postedBy(user)
                .build();

        Question saved = questionRepository.save(question);

        // Observer Pattern: notify all listeners that a question was posted
        eventPublisher.publishQuestionPosted(QuestionEvent.posted(saved));

        return QuestionResponse.from(saved);
    }

    @Override
    public List<QuestionResponse> getAllQuestions(String sortBy) {
        List<Question> questions = questionRepository.findAllByOrderByCreatedAtDesc();

        // Strategy Pattern: pick sorting algorithm dynamically
        QuestionSortStrategy strategy = sortStrategies.getOrDefault(
                sortBy != null ? sortBy.toLowerCase() : "newest",
                sortStrategies.get("newest"));

        return strategy.sort(questions).stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<QuestionResponse> getMyQuestions(String email) {
        User user = getUser(email);
        return questionRepository.findByPostedByOrderByCreatedAtDesc(user)
                .stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    @SuppressWarnings("null")
    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Question not found with id: " + id));
        return QuestionResponse.from(question);
    }

    @Override
    public List<QuestionResponse> getQuestionsBySubject(Question.Subject subject) {
        return questionRepository.findBySubject(subject)
                .stream()
                .map(QuestionResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAllSubjects() {
        return Arrays.stream(Question.Subject.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found: " + email));
    }
}
