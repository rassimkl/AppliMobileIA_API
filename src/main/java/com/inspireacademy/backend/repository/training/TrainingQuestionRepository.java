package com.inspireacademy.backend.repository.training;

import com.inspireacademy.backend.model.training.TrainingQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingQuestionRepository extends JpaRepository<TrainingQuestion, Long> {

    List<TrainingQuestion> findByTrainingId(Long trainingId);
}