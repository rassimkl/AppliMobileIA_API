package com.inspireacademy.backend.dto.tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminTestResponse {
    private Long id;
    private String title;
    private String description;
    private String langueName;
    private Integer durationSeconds;
    private boolean published;
}