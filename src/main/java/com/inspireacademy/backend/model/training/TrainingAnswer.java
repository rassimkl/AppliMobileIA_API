package com.inspireacademy.backend.model.training;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "training_answers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"training_attempt_id", "question_id"})
)
public class TrainingAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_attempt_id", nullable = false)
    private TrainingAttempt trainingAttempt;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private TrainingQuestion question;

    @ManyToOne
    @JoinColumn(name = "selected_option_id")
    private TrainingOption selectedOption;

    @Column(length = 2000)
    private String textAnswer;

    @Builder.Default
    private Integer awardedPoints = 0;
}