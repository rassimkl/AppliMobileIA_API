package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingAssignmentRepository extends JpaRepository<TrainingAssignment, Long> {

    List<TrainingAssignment> findByStudentId(Long studentId);

    List<TrainingAssignment> findByTrainingId(Long trainingId);

    Optional<TrainingAssignment> findByTrainingIdAndStudentId(Long trainingId, Long studentId);
}