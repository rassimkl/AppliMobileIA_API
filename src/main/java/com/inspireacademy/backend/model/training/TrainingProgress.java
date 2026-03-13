package com.inspireacademy.backend.model.training;

import com.inspireacademy.backend.model.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "training_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"training_id", "student_id"})
)
public class TrainingProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Builder.Default
    private Integer progressPercent = 0;

    @Builder.Default
    private boolean completed = false;

    private LocalDateTime lastOpenedAt;

    @PrePersist
    public void onCreate() {
        if (lastOpenedAt == null) {
            lastOpenedAt = LocalDateTime.now();
        }
    }
}