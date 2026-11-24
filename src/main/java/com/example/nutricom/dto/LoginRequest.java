package com.example.nutricom.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El campo email es obligatorio")
        @Email(message = "El email no es válido") String email,
        @NotBlank(message = "El campo password es obligatorio") String password,
        String role) {
}
