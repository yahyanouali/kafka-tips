package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Simple user DTO used by the REST API and cache layer.
 */
public record UserDto(String name, int age) {

    /**
     * Validates basic invariants:
     * - name must be non-null and non-blank
     * - age must be non-negative
     */
    @JsonIgnore
    public boolean isValid() {
        return this.name() != null && !this.name().isBlank() && this.age() >= 0;
    }
}
