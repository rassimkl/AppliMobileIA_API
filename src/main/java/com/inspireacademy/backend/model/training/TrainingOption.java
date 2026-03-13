package com.inspireacademy.backend.model.training;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "training_options")
public class TrainingOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * utile pour ORDER_WORDS / ORDER_SENTENCE
     * représente la bonne position dans l'ordre attendu
     */
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private TrainingQuestion question;

    @Column(nullable = false, length = 1000)
    private String content;

    /**
     * utile uniquement pour QCM
     */
    @Builder.Default
    private boolean correct = false;
}