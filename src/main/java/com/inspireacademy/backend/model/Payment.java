package com.inspireacademy.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private String status; // PAID, FAILED

    @Column(nullable = false)
    private String provider; // STRIPE

    @Column(unique = true)
    private String checkoutSessionId;

    @Column(unique = true)
    private String paymentIntentId;

    @Column(nullable = false)
    private String currency; // "eur"

    @Column(unique = true)
    private String stripeEventId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

}