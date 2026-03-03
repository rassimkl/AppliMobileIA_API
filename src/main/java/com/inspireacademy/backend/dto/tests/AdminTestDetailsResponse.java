package com.inspireacademy.backend.dto.tests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTestDetailsResponse {
    private Long id;
    private String title;
    private String description;
    private String langueName;
    private Integer durationSeconds;
    private boolean published;
    private List<AdminQuestionResponse> questions;
}