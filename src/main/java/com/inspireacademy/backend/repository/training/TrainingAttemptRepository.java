package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrainingAttemptRepository extends JpaRepository<TrainingAttempt, Long> {

    List<TrainingAttempt> findByStudentId(Long studentId);

    List<TrainingAttempt> findByTrainingId(Long trainingId);

    Optional<TrainingAttempt> findTopByTrainingIdAndStudentIdOrderByCompletedAtDesc(Long trainingId, Long studentId);

    Optional<TrainingAttempt> findTopByTrainingIdAndStudentIdOrderByAttemptNumberDesc(Long trainingId, Long studentId);

    List<TrainingAttempt> findByStudent_Teacher_Id(Long teacherId);
}