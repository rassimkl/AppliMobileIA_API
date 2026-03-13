package com.inspireacademy.backend.service.mapper;

import com.inspireacademy.backend.dto.training.AdminTrainingResponse;
import com.inspireacademy.backend.dto.training.TrainingResponse;
import com.inspireacademy.backend.model.training.Training;

public class TrainingMapper {

    private TrainingMapper() {
    }

    public static AdminTrainingResponse toAdminResponse(Training training) {
        return AdminTrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langueId(training.getLangue() != null ? training.getLangue().getId() : null)
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .visibility(training.getVisibility())
                .published(training.isPublished())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .createdById(training.getCreatedBy() != null ? training.getCreatedBy().getId() : null)
                .createdByEmail(training.getCreatedBy() != null ? training.getCreatedBy().getEmail() : null)
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }

    public static TrainingResponse toResponse(Training training) {
        return TrainingResponse.builder()
                .id(training.getId())
                .title(training.getTitle())
                .description(training.getDescription())
                .langueId(training.getLangue() != null ? training.getLangue().getId() : null)
                .langue(training.getLangue() != null ? training.getLangue().getName() : null)
                .type(training.getType())
                .visibility(training.getVisibility())
                .published(training.isPublished())
                .durationSeconds(training.getDurationSeconds())
                .fileUrl(training.getFileUrl())
                .coverUrl(training.getCoverUrl())
                .level(training.getLevel())
                .documentCategory(training.getDocumentCategory())
                .customCategory(training.getCustomCategory())
                .createdById(training.getCreatedBy() != null ? training.getCreatedBy().getId() : null)
                .createdByEmail(training.getCreatedBy() != null ? training.getCreatedBy().getEmail() : null)
                .createdAt(training.getCreatedAt())
                .updatedAt(training.getUpdatedAt())
                .build();
    }
}