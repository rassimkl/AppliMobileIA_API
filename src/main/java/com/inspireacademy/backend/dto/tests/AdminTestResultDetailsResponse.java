package com.inspireacademy.backend.dto.tests;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class AdminTestResultDetailsResponse {

    private Long resultId;
    private String studentEmail;
    private String studentFirstName;
    private String studentLastName;
    private Integer score;
    private Integer maxScore;
    private LocalDateTime completedAt;

    private List<AdminAnswerDetailsResponse> answers;

}