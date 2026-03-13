package com.inspireacademy.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads")
@RequiredArgsConstructor
public class UploadController {

    private static final String UPLOAD_DIR = "uploads";

    @PostMapping("/documents")
    @PreAuthorize("hasAnyRole('ADMIN','ENSEIGNANT')")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", "Fichier vide")
            );
        }

        // créer dossier uploads si inexistant
        Path uploadPath = Paths.get(UPLOAD_DIR);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // générer nom unique
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return ResponseEntity.ok(
                Map.of(
                        "url",
                        "/uploads/" + fileName
                )
        );
    }
}