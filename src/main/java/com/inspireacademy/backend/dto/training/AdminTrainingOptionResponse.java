package com.inspireacademy.backend.dto.training;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTrainingOptionResponse {

    private Long id;
    private String content;

    /**
     * utile pour QCM
     */
    private boolean correct;

    /**
     * utile pour ORDER_WORDS / ORDER_SENTENCE
     */
    private Integer position;
}