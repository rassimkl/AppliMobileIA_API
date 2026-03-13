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
        name = "training_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"training_id", "student_id"})
)
public class TrainingAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne
    @JoinColumn(name = "assigned_by_id", nullable = false)
    private User assignedBy;

    @Builder.Default
    private boolean completed = false;

    @Builder.Default
    private boolean mandatory = true;

    private LocalDateTime assignedAt;

    private LocalDateTime dueDate;

    @PrePersist
    public void onAssign() {
        assignedAt = LocalDateTime.now();
    }
}