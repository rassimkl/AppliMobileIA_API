package com.inspireacademy.backend.dto.tests;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OptionResponse {

    private Long id;
    private String content;
}