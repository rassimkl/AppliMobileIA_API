package com.inspireacademy.backend.model.training;

import com.inspireacademy.backend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "training_attempts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"training_id", "student_id", "attempt_number"})
)
public class TrainingAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer attemptNumber;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private Integer score;
    private Integer maxScore;

    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "trainingAttempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingAnswer> answers = new ArrayList<>();
}