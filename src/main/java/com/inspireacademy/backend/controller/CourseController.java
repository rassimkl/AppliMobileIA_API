package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.dto.CourseResponse;
import com.inspireacademy.backend.dto.CreateCourseRequest;
import com.inspireacademy.backend.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public CourseResponse createCourse(
            @RequestBody CreateCourseRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName(); // récupéré depuis le JWT
        return courseService.createCourse(request, email);
    }

    @GetMapping
    public List<CourseResponse> getCourses() {
        return courseService.getPublishedCourses();
    }

    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
        return courseService.getCourseById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public CourseResponse updateCourse(
            @PathVariable Long id,
            @RequestBody CreateCourseRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return courseService.updateCourse(id, request, email);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }
}
