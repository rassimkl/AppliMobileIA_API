package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.Training;
import com.inspireacademy.backend.model.training.TrainingType;
import com.inspireacademy.backend.model.training.TrainingVisibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TrainingRepository extends JpaRepository<Training, Long> {

    List<Training> findByPublishedTrue();

    List<Training> findByCreatedById(Long createdById);

    List<Training> findByCreatedByIdAndLangueId(Long createdById, Long langueId);

    List<Training> findByLangueIdAndPublishedTrueAndVisibility(Long langueId, TrainingVisibility visibility);

    List<Training> findByLangueIdAndPublishedTrueAndVisibilityAndType(
            Long langueId,
            TrainingVisibility visibility,
            TrainingType type
    );

    List<Training> findByLangueIdInAndPublishedTrueAndVisibility(
            Collection<Long> langueIds,
            TrainingVisibility visibility
    );
}