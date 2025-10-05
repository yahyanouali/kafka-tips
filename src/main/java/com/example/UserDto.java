package com.example;

public record UserDto(String name, int age) {

    public boolean isValid() {
        return  this.name() != null && !this.name().isBlank() || this.age() > 0;
    }
}
