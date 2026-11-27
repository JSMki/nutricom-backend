package com.example.nutricom.dto;

import com.example.nutricom.models.User;

import java.util.List;

public record UsuarioResponse(int id, String name, String email, String role, List<String> roles) {

    public static UsuarioResponse de(User usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getRol(),
                List.of(usuario.getRol()));
    }
}
