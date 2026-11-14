package com.example.nutricom.services;

import com.example.nutricom.dto.CreateUserRequest;
import com.example.nutricom.models.User;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final SinglyLinkedList<User> usuarios = new SinglyLinkedList<>();
    private int nextId = 1;

    public UserService() {
        // Datos de ejemplo obligatorios por rúbrica
        createUser(new CreateUserRequest("ana_gomez", "ana@email.com", "hash123", "555-0001", "activo"));
        createUser(new CreateUserRequest("org_caritas", "caritas@org.com", "hash456", "555-0002", "activo"));
        createUser(new CreateUserRequest("super_puebla", "contacto@superpuebla.mx", "hash789", "555-0003", "activo"));
    }

    public List<User> getAllUsers() {
        return usuarios.toList();
    }

    public Optional<User> getUserById(int id) {
        return usuarios.find(u -> u.getIdUsuario() == id);
    }

    public User createUser(CreateUserRequest request) {
        User newUser = new User(nextId++, request.nombreUsuario(), request.correo(), request.contrasenaHash(), request.telefono(), request.estado());
        usuarios.insertLast(newUser);
        return newUser;
    }

    public boolean deleteUser(int id) {
        return usuarios.removeIf(u -> u.getIdUsuario() == id);
    }
}