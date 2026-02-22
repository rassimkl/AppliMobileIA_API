package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.UserResponse;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    // Injection par constructeur
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Méthode métier simple
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getRole().name(),
                        user.getEnabled(),
                        user.getCreatedAt()
                ))
                .toList();
    }

    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getEnabled(),
                user.getCreatedAt()
        );
    }

}
