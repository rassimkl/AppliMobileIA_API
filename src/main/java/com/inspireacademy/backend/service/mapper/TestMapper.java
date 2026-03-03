package com.inspireacademy.backend.service.mapper;

import com.inspireacademy.backend.dto.tests.AdminTestResponse;
import com.inspireacademy.backend.dto.tests.TestAssignmentResponse;
import com.inspireacademy.backend.model.tests.Test;
import com.inspireacademy.backend.model.tests.TestAssignment;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestMapper {

    public static AdminTestResponse toAdminTestResponse(Test test) {
        return AdminTestResponse.builder()
                .id(test.getId())
                .title(test.getTitle())
                .description(test.getDescription())
                .langueName(test.getLangue().getName())
                .durationSeconds(test.getDurationSeconds())
                .published(test.isPublished())
                .build();
    }

    public static TestAssignmentResponse toAssignmentResponse(TestAssignment a) {
        return TestAssignmentResponse.builder()
                .testId(a.getTest().getId())
                .title(a.getTest().getTitle())
                .langue(a.getTest().getLangue().getName())
                .durationSeconds(a.getTest().getDurationSeconds())
                .completed(a.isCompleted())
                .build();
    }
}