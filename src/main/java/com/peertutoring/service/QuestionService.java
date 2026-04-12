package com.peertutoring.service;

import com.peertutoring.dto.Dtos.*;
import com.peertutoring.model.Question;
import java.util.List;

public interface QuestionService {
    QuestionResponse postQuestion(PostQuestionRequest request, String email);
    List<QuestionResponse> getAllQuestions(String sortBy);
    List<QuestionResponse> getMyQuestions(String email);
    QuestionResponse getQuestionById(Long id);
    List<QuestionResponse> getQuestionsBySubject(Question.Subject subject);
    List<String> getAllSubjects();
}
