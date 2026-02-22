package com.inspireacademy.backend.service;

import com.inspireacademy.backend.dto.AuthResponse;
import com.inspireacademy.backend.dto.RegisterRequest;
import com.inspireacademy.backend.model.Role;
import com.inspireacademy.backend.model.User;
import com.inspireacademy.backend.repository.UserRepository;
import com.inspireacademy.backend.security.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // ==========================
    // LOGIN
    // ==========================
    public AuthResponse login(String email, String password) {

        // 1️⃣ Chercher l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Email incorrect")
                );

        // 2️⃣ Vérifier le mot de passe sécurisé
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Mot de passe incorrect");
        }

        // 3️⃣ Vérifier que le compte est actif
        if (!user.getEnabled()) {
            throw new RuntimeException("Compte désactivé");
        }

        // 4️⃣ Générer le token JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // 5️⃣ Retourner la réponse
        return new AuthResponse(token);
    }

    // ==========================
    // REGISTER
    // ==========================
    public AuthResponse register(RegisterRequest request) {

        // 1️⃣ Vérifier si l'email existe déjà
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // 2️⃣ Créer l’utilisateur
        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        // 3️⃣ Hasher le mot de passe (TRÈS IMPORTANT)
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // 4️⃣ Valeurs par défaut
        user.setRole(Role.ETUDIANT);
        user.setEnabled(true);

        // 5️⃣ Sauvegarde en base
        userRepository.save(user);

        // 6️⃣ Générer le token JWT
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(token);
    }
}
