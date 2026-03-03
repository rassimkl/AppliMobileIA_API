package com.inspireacademy.backend.dto.tests;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminQuestionResponse {
    private Long id;
    private String content;
    private String type;
    private Integer points;
    private List<AdminOptionResponse> options;
}