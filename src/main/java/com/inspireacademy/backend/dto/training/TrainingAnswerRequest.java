package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingAnswerRequest {

    private Long questionId;

    /**
     * utile uniquement pour QCM
     */
    private Long selectedOptionId;

    /**
     * utile pour TEXT
     */
    private String textAnswer;

    /**
     * utile pour ORDER_WORDS / ORDER_SENTENCE
     * contient les IDs des options dans l'ordre choisi par l'étudiant
     */
    private List<Long> orderedOptionIds;
}