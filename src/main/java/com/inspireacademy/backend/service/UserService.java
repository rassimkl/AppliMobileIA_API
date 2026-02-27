package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.CreateUserRequest;
import com.inspireacademy.backend.dto.UserResponse;
import com.inspireacademy.backend.dto.UpdateUserRequest;
import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.model.Role;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.LangueRepository;
import com.inspireacademy.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LangueRepository langueRepository;

    // Injection par constructeur
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, LangueRepository langueRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.langueRepository = langueRepository;
    }

    // Méthode métier simple
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse createUserByAdmin(CreateUserRequest request) {

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(Role.valueOf(request.getRole()));
        Set<Langue> languages = new HashSet<>(
                langueRepository.findAllById(request.getLanguages())
        );

        user.setLanguages(languages);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);

        return mapToResponse(user);
    }

    public List<UserResponse> getUsersByRole(Role role) {
        return userRepository.findByRole(role)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return mapToResponse(user);
    }

    // 🔥 UPDATE USER
    public UserResponse updateUser(Long id, UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (request.getFirstName() != null)
            user.setFirstName(request.getFirstName());

        if (request.getLastName() != null)
            user.setLastName(request.getLastName());

        if (request.getEmail() != null)
            user.setEmail(request.getEmail());

        if (request.getLanguages() != null) {

            Set<Langue> languages = new HashSet<>(
                    langueRepository.findAllById(request.getLanguages())
            );

            user.setLanguages(languages);
        }

        userRepository.save(user);

        return mapToResponse(user);
    }

    // 🔥 DELETE USER
    public void deleteUser(Long id) {

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }

        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {

        Set<String> languageNames = user.getLanguages()
                .stream()
                .map(Langue::getName)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                languageNames,
                user.getEnabled(),
                user.getCreatedAt()
        );
    }

}
