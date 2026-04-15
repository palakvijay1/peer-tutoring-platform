package com.peertutoring.controller;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.model.Question;
import com.peertutoring.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<ApiResponse<QuestionResponse>> postQuestion(
            @Valid @RequestBody PostQuestionRequest request,
            @RequestHeader("X-User-Email") String email) {
        QuestionResponse response = questionService.postQuestion(request, email);
        return ResponseEntity.ok(ApiResponse.ok("Question posted successfully!", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getAllQuestions(
            @RequestParam(defaultValue = "newest") String sortBy) {
        List<QuestionResponse> questions = questionService.getAllQuestions(sortBy);
        return ResponseEntity.ok(ApiResponse.ok("Questions fetched!", questions));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getMyQuestions(
            @RequestHeader("X-User-Email") String email) {
        List<QuestionResponse> questions = questionService.getMyQuestions(email);
        return ResponseEntity.ok(ApiResponse.ok("Your questions!", questions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<QuestionResponse>> getQuestionById(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestionById(id);
        return ResponseEntity.ok(ApiResponse.ok("Question found!", response));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<String>>> getAllSubjects() {
        List<String> subjects = questionService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.ok("Subjects fetched!", subjects));
    }

    @GetMapping("/subject/{subject}")
    public ResponseEntity<ApiResponse<List<QuestionResponse>>> getBySubject(
            @PathVariable Question.Subject subject) {
        List<QuestionResponse> questions = questionService.getQuestionsBySubject(subject);
        return ResponseEntity.ok(ApiResponse.ok("Questions for subject!", questions));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuestion(
            @PathVariable Long id,
            @RequestHeader("X-User-Email") String email) {
 
        questionService.deleteQuestion(id, email);
 
        return ResponseEntity.ok(
                ApiResponse.ok("Question deleted successfully!", null));
    }
}