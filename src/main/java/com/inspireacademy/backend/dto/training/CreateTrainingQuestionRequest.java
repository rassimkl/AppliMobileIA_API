package com.inspireacademy.backend.dto.training;

import com.inspireacademy.backend.model.training.TrainingQuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTrainingQuestionRequest {

    private String content;

    /**
     * QCM, TEXT, ORDER_WORDS, ORDER_SENTENCE
     */
    private TrainingQuestionType type;

    private Integer points;

    /**
     * utile uniquement pour TEXT
     */
    private String expectedAnswer;

    private Boolean caseSensitive;

    /**
     * utile pour :
     * - QCM
     * - ORDER_WORDS
     * - ORDER_SENTENCE
     */
    private List<TrainingOptionRequest> options;
}