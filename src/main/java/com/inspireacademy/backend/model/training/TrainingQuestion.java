package com.inspireacademy.backend.model.training;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "training_questions")
public class TrainingQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ordre d'affichage de la question dans l'exercice
     */
    private Integer position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "training_id", nullable = false)
    private Training training;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingQuestionType type;

    @Builder.Default
    private Integer points = 0;

    /**
     * utile uniquement pour TEXT
     */
    @Column(length = 2000)
    private String expectedAnswer;

    @Builder.Default
    private boolean caseSensitive = false;

    /**
     * pour :
     * - QCM -> options + une correcte
     * - ORDER_WORDS -> mots à remettre dans l'ordre
     * - ORDER_SENTENCE -> phrases à remettre dans l'ordre
     *
     * pour ORDER_* : le bon ordre est donné par "position" dans TrainingOption
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingOption> options = new ArrayList<>();
}