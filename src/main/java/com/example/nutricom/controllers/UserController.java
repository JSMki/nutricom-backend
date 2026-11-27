package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateUserRequest;
import com.example.nutricom.models.User;
import com.example.nutricom.security.RequiereRol;
import com.example.nutricom.security.Roles;
import com.example.nutricom.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UserController {
    public record ApiMessage(String mensaje) {
    }

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    @RequiereRol(Roles.ADMIN)
    public List<User> listar() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    @RequiereRol(Roles.ADMIN)
    public ResponseEntity<?> buscar(@PathVariable int id) {
        var usuario = service.getUserById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(404).body(new ApiMessage("Usuario no encontrado"));
    }

    @PostMapping
    @RequiereRol(Roles.ADMIN)
    public ResponseEntity<User> crear(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(service.createUser(request));
    }

    @DeleteMapping("/{id}")
    @RequiereRol(Roles.ADMIN)
    public ResponseEntity<ApiMessage> eliminar(@PathVariable int id) {
        if (service.deleteUser(id)) {
            return ResponseEntity.ok(new ApiMessage("Usuario eliminado correctamente"));
        }
        return ResponseEntity.status(404).body(new ApiMessage("Usuario no encontrado"));
    }
}