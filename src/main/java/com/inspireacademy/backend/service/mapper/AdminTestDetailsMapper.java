package com.inspireacademy.backend.service.mapper;

import com.inspireacademy.backend.dto.tests.*;
import com.inspireacademy.backend.model.tests.QuestionType;
import com.inspireacademy.backend.model.tests.Test;
import lombok.experimental.UtilityClass;

import java.util.List;

@UtilityClass
public class AdminTestDetailsMapper {

    public static AdminTestDetailsResponse toAdminDetails(Test test) {

        List<AdminQuestionResponse> questions = test.getQuestions().stream().map(q -> {
            List<AdminOptionResponse> options = null;

            if (q.getType() == QuestionType.QCM) {
                options = q.getOptions().stream()
                        .map(o -> AdminOptionResponse.builder()
                                .id(o.getId())
                                .content(o.getContent())
                                .correct(o.isCorrect())
                                .build())
                        .toList();
            }

            return AdminQuestionResponse.builder()
                    .id(q.getId())
                    .content(q.getContent())
                    .type(q.getType().name())
                    .points(q.getPoints())
                    .options(options)
                    .build();
        }).toList();

        return AdminTestDetailsResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .langueName(test.getLangue().getName())
                .durationSeconds(test.getDurationSeconds())
                .published(test.isPublished())
                .questions(questions)
                .build();
    }
}