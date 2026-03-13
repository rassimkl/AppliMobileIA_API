package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingProgressRepository extends JpaRepository<TrainingProgress, Long> {

    Optional<TrainingProgress> findByTrainingIdAndStudentId(Long trainingId, Long studentId);

    List<TrainingProgress> findByStudentId(Long studentId);
}