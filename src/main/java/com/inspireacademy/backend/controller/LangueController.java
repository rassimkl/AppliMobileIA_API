package com.inspireacademy.backend.controller;

import com.inspireacademy.backend.model.Langue;
import com.inspireacademy.backend.service.LangueService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/langues")
@CrossOrigin
public class LangueController {

    private final LangueService langueService;

    public LangueController(LangueService langueService) {
        this.langueService = langueService;
    }

    // ✅ GET ALL (tout le monde peut voir)
    @GetMapping
    public List<Langue> getAll() {
        return langueService.getAll();
    }

    // ✅ CREATE (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Langue create(@RequestBody Langue request) {
        return langueService.create(request.getName());
    }

    // ✅ DELETE (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        langueService.delete(id);
    }
}