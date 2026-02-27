package com.inspireacademy.backend.service;

import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.repository.LangueRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LangueService {

    private final LangueRepository langueRepository;

    public LangueService(LangueRepository langueRepository) {
        this.langueRepository = langueRepository;
    }

    // ✅ GET ALL
    public List<Langue> getAll() {
        return langueRepository.findAll();
    }

    // ✅ CREATE
    public Langue create(String name) {

        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Nom de langue obligatoire");
        }

        String cleaned = name.trim();

        // Optionnel: éviter doublons (ex: Français vs français)
        boolean exists = langueRepository.existsByNameIgnoreCase(cleaned);
        if (exists) {
            throw new RuntimeException("Cette langue existe déjà");
        }

        Langue langue = new Langue();
        langue.setName(cleaned);
        return langueRepository.save(langue);
    }

    // ✅ DELETE
    public void delete(Long id) {

        if (!langueRepository.existsById(id)) {
            throw new RuntimeException("Langue introuvable");
        }

        langueRepository.deleteById(id);
    }
}