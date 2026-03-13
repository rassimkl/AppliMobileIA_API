package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubmitTrainingRequest {
    private List<TrainingAnswerRequest> answers;
}