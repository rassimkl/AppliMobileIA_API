package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.CreatePlanRequest;
import com.inspireacademy.backend.dto.PlanResponse;
import com.inspireacademy.backend.model.Course;
import com.inspireacademy.backend.model.Plan;
import com.inspireacademy.backend.repository.CourseRepository;
import com.inspireacademy.backend.repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlanService {

    private final PlanRepository planRepository;
    private final CourseRepository courseRepository;

    public PlanService(PlanRepository planRepository,
                       CourseRepository courseRepository) {
        this.planRepository = planRepository;
        this.courseRepository = courseRepository;
    }

    // =============================
    // CREATE PLAN
    // =============================
    public PlanResponse createPlan(Long courseId, CreatePlanRequest request) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        validatePlanRequest(request);

        Plan plan = new Plan();
        plan.setCourse(course);
        plan.setNumberOfHours(request.getNumberOfHours());
        plan.setType(request.getType());
        plan.setPrice(request.getPrice());

        if ("INDIVIDUAL".equals(request.getType())) {
            plan.setMaxParticipants(null);
        } else {
            plan.setMaxParticipants(request.getMaxParticipants());
        }

        Plan savedPlan = planRepository.save(plan);

        return mapToResponse(savedPlan);
    }

    // =============================
    // GET ALL PLANS
    // =============================
    public List<PlanResponse> getAllPlans() {

        return planRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // =============================
    // GET PLAN BY ID
    // =============================
    public PlanResponse getPlanById(Long id) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        return mapToResponse(plan);
    }

    // =============================
    // UPDATE PLAN
    // =============================
    public PlanResponse updatePlan(Long id, CreatePlanRequest request) {

        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Plan not found"));

        validatePlanRequest(request);

        plan.setNumberOfHours(request.getNumberOfHours());
        plan.setType(request.getType());
        plan.setPrice(request.getPrice());

        if ("INDIVIDUAL".equals(request.getType())) {
            plan.setMaxParticipants(null);
        } else {
            plan.setMaxParticipants(request.getMaxParticipants());
        }

        Plan updatedPlan = planRepository.save(plan);

        return mapToResponse(updatedPlan);
    }

    // =============================
    // DELETE PLAN
    // =============================
    public void deletePlan(Long id) {

        if (!planRepository.existsById(id)) {
            throw new RuntimeException("Plan not found");
        }

        planRepository.deleteById(id);
    }

    // =============================
    // VALIDATION COMMUNE
    // =============================
    private void validatePlanRequest(CreatePlanRequest request) {

        if ("GROUP".equals(request.getType())) {
            if (request.getMaxParticipants() == null || request.getMaxParticipants() < 2) {
                throw new RuntimeException("A group plan must have at least 2 participants");
            }
        }

        if (request.getNumberOfHours() <= 0) {
            throw new RuntimeException("Number of hours must be greater than 0");
        }

        if (request.getPrice() <= 0) {
            throw new RuntimeException("Price must be greater than 0");
        }
    }

    // =============================
    // MAPPER ENTITY → DTO
    // =============================
    private PlanResponse mapToResponse(Plan plan) {

        PlanResponse response = new PlanResponse();

        response.setId(plan.getId());
        response.setCourseTitle(plan.getCourse().getTitle());
        response.setLanguage(plan.getCourse().getLanguage());
        response.setLevel(plan.getCourse().getLevel());
        response.setNumberOfHours(plan.getNumberOfHours());
        response.setType(plan.getType());
        response.setMaxParticipants(plan.getMaxParticipants());
        response.setPrice(plan.getPrice());

        return response;
    }
}