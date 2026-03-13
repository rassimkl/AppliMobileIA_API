package com.inspireacademy.backend.model.training;

import com.inspireacademy.backend.model.Langue;
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
@Table(name = "trainings")
public class Training {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(length = 2000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "langue_id", nullable = false)
    private Langue langue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrainingType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TrainingVisibility visibility = TrainingVisibility.ASSIGNED_ONLY;

    @Builder.Default
    private boolean published = false;

    /**
     * utile uniquement pour EXERCISE
     */
    private Integer durationSeconds;

    /**
     * utile uniquement pour DOCUMENT
     * on stocke une URL / chemin du PDF
     */
    @Column(length = 2000)
    private String fileUrl;

    @Column(length = 2000)
    private String coverUrl;

    /**
     * ex: A1, A2, B1...
     */
    @Column(length = 100)
    private String level;

    /**
     * utile uniquement pour DOCUMENT
     * COURSE, EXTRA_EXERCISES, BOOK, OTHER
     */
    @Enumerated(EnumType.STRING)
    private DocumentCategory documentCategory;

    /**
     * utilisé seulement si documentCategory = OTHER
     * ex: "Poésie", "Révision orale", etc.
     */
    @Column(length = 150)
    private String customCategory;

    /**
     * questions seulement pour EXERCISE
     */
    @OneToMany(mappedBy = "training", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TrainingQuestion> questions = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}