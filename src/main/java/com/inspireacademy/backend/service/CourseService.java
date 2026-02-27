package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.CourseResponse;
import com.inspireacademy.backend.dto.CreateCourseRequest;
import com.inspireacademy.backend.model.Course;
import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.CourseRepository;
import com.inspireacademy.backend.repository.LangueRepository;
import com.inspireacademy.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LangueRepository langueRepository;

    public CourseService(
            CourseRepository courseRepository,
            UserRepository userRepository,
            LangueRepository langueRepository
    ) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.langueRepository = langueRepository;
    }

    // 🔥 CREATE COURSE
    public CourseResponse createCourse(CreateCourseRequest request, String email) {

        // 🔎 récupérer admin depuis DB
        User admin = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // 🔎 récupérer la langue depuis DB (FK)
        Langue langue = langueRepository.findById(request.getLangueId())
                .orElseThrow(() -> new RuntimeException("Langue introuvable"));

        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLangue(langue);
        course.setLevel(request.getLevel());
        course.setStatus("PUBLISHED");
        course.setCreatedBy(admin.getId());
        course.setCreatedAt(LocalDateTime.now());
        course.setUpdatedAt(LocalDateTime.now());

        Course saved = courseRepository.save(course);

        return mapToResponse(saved);
    }

    // 🔥 GET PUBLISHED COURSES
    public List<CourseResponse> getPublishedCourses() {
        return courseRepository.findByStatus("PUBLISHED")
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        return mapToResponse(course);
    }

    public CourseResponse updateCourse(Long id, CreateCourseRequest request, String email) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cours non trouvé"));

        Langue langue = langueRepository.findById(request.getLangueId())
                .orElseThrow(() -> new RuntimeException("Langue introuvable"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setLangue(langue);
        course.setLevel(request.getLevel());
        course.setUpdatedAt(LocalDateTime.now());

        Course updated = courseRepository.save(course);

        return mapToResponse(updated);
    }

    // 🔥 DELETE COURSE
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new RuntimeException("Cours introuvable");
        }
        courseRepository.deleteById(id);
    }

    // 🔥 ENTITY → DTO
    private CourseResponse mapToResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getLangue().getId(),
                course.getLangue().getName(),
                course.getLevel(),
                course.getStatus(),
                course.getCreatedAt()
        );
    }
}