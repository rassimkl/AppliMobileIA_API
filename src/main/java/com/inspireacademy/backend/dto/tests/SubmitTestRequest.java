package com.inspireacademy.backend.dto.tests;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubmitTestRequest {

    private List<AnswerRequest> answers;
}