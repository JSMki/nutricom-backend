package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateUserRequest;
import com.example.nutricom.models.User;
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
    public List<User> listar() {
        return service.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable int id) {
        var usuario = service.getUserById(id);
        if (usuario.isPresent()) {
            return ResponseEntity.ok(usuario.get());
        }
        return ResponseEntity.status(404).body(new ApiMessage("Usuario no encontrado"));
    }

    @PostMapping
    public ResponseEntity<User> crear(@RequestBody CreateUserRequest request) {
        return ResponseEntity.status(201).body(service.createUser(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiMessage> eliminar(@PathVariable int id) {
        if (service.deleteUser(id)) {
            return ResponseEntity.ok(new ApiMessage("Usuario eliminado correctamente"));
        }
        return ResponseEntity.status(404).body(new ApiMessage("Usuario no encontrado"));
    }
}