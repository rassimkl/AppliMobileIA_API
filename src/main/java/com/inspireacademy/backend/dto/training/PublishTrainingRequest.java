package com.inspireacademy.backend.dto.training;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublishTrainingRequest {
    private boolean published;
}