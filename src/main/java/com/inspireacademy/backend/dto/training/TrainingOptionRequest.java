package com.inspireacademy.backend.dto.training;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingOptionRequest {

    private String content;

    /**
     * utile uniquement pour QCM
     */
    private Boolean correct;

    /**
     * utile pour ORDER_WORDS / ORDER_SENTENCE
     * représente la bonne position dans l'ordre attendu
     */
    private Integer position;
}