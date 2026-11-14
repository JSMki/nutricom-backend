package com.example.nutricom.dto;

public record CreateUserRequest(
        String nombreUsuario,
        String correo,
        String contrasenaHash,
        String telefono,
        String estado
) {}