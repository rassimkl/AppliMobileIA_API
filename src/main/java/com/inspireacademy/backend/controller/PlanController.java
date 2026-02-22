package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.CreatePlanRequest;
import com.inspireacademy.backend.dto.PlanResponse;
import com.inspireacademy.backend.service.PlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    // =============================
    // CREATE PLAN (ADMIN)
    // =============================
    @PostMapping("/{courseId}")
    public PlanResponse createPlan(
            @PathVariable Long courseId,
            @RequestBody CreatePlanRequest request) {

        return planService.createPlan(courseId, request);
    }

    // =============================
    // GET ALL PLANS
    // =============================
    @GetMapping
    public List<PlanResponse> getPlans() {
        return planService.getAllPlans();
    }

    // =============================
    // GET PLAN BY ID
    // =============================
    @GetMapping("/{id}")
    public PlanResponse getPlanById(@PathVariable Long id) {
        return planService.getPlanById(id);
    }

    // =============================
    // UPDATE PLAN (ADMIN)
    // =============================
    @PutMapping("/{id}")
    public PlanResponse updatePlan(
            @PathVariable Long id,
            @RequestBody CreatePlanRequest request) {

        return planService.updatePlan(id, request);
    }

    // =============================
    // DELETE PLAN (ADMIN)
    // =============================
    @DeleteMapping("/{id}")
    public void deletePlan(@PathVariable Long id) {
        planService.deletePlan(id);
    }
}