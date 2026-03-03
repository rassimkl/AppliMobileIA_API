package com.inspireacademy.backend.dto.tests;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOptionResponse {
    private Long id;
    private String content;
    private boolean correct;
}