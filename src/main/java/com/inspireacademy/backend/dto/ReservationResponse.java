package com.inspireacademy.backend.dto;

import java.time.LocalDateTime;

public class ReservationResponse {

    private Long id;
    private String status;
    private String userEmail;
    private String courseTitle;
    private Double planPrice;
    private LocalDateTime createdAt;
    private boolean paid;

    public ReservationResponse(
            Long id,
            String status,
            String userEmail,
            String courseTitle,
            Double planPrice,
            LocalDateTime createdAt,
            boolean paid
    ) {
        this.id = id;
        this.status = status;
        this.userEmail = userEmail;
        this.courseTitle = courseTitle;
        this.planPrice = planPrice;
        this.createdAt = createdAt;
        this.paid = paid;
    }

    public Long getId() { return id; }
    public String getStatus() { return status; }
    public String getUserEmail() { return userEmail; }
    public String getCourseTitle() { return courseTitle; }
    public Double getPlanPrice() { return planPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public boolean isPaid() { return paid; }
}