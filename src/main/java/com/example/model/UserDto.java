package com.example.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public record UserDto(String name, int age) {

    @JsonIgnore
    public boolean isValid() {
        return  this.name() != null && !this.name().isBlank() || this.age() > 0;
    }
}
