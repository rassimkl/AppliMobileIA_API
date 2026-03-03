package com.inspireacademy.backend.model.tests;

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
@Table(name = "test_assignments",
        uniqueConstraints = @UniqueConstraint(columnNames = {"test_id", "student_id"}))
public class TestAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    private boolean completed = false;

    private LocalDateTime assignedAt;

    @PrePersist
    public void onAssign() {
        assignedAt = LocalDateTime.now();
    }
}