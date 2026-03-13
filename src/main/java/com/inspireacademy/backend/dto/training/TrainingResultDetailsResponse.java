package com.inspireacademy.backend.dto.training;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainingResultDetailsResponse {

    private Long attemptId;

    private String studentEmail;
    private String studentFirstName;
    private String studentLastName;

    private Integer score;
    private Integer maxScore;

    private LocalDateTime completedAt;

    private List<TrainingAnswerDetailsResponse> answers;
}