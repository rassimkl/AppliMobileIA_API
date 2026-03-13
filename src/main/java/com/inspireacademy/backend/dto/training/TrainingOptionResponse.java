package com.inspireacademy.backend.dto.training;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingOptionResponse {

    private Long id;
    private String content;

    /**
     * utile pour ORDER_WORDS / ORDER_SENTENCE
     * selon ton front, tu peux choisir de l'utiliser
     * pour mélanger puis reconstituer l'ordre attendu
     */
    private Integer position;
}