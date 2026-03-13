package com.inspireacademy.backend.dto;

public class LangueSimpleResponse {

    private Long id;
    private String name;

    public LangueSimpleResponse(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}