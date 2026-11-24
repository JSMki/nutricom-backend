package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateOrganizationRequest;
import com.example.nutricom.models.Organization;
import com.example.nutricom.security.RequiereRol;
import com.example.nutricom.security.Roles;
import com.example.nutricom.services.OrganizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
public class OrganizationController {
    public record ApiMessage(String mensaje) {
    }

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }

    @GetMapping
    @RequiereRol({Roles.ADMIN, Roles.ORGANIZACION})
    public List<Organization> listar() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    @RequiereRol({Roles.ADMIN, Roles.ORGANIZACION})
    public ResponseEntity<?> buscar(@PathVariable int id) {
        var organizacion = service.getById(id);
        if (organizacion.isPresent()) {
            return ResponseEntity.ok(organizacion.get());
        }
        return ResponseEntity.status(404).body(new ApiMessage("Organización no encontrada"));
    }

    @PostMapping
    @RequiereRol({Roles.ADMIN, Roles.ORGANIZACION})
    public ResponseEntity<Organization> crear(@RequestBody CreateOrganizationRequest request) {
        return ResponseEntity.status(201).body(service.create(request));
    }

    @GetMapping("/pendientes")
    @RequiereRol({Roles.ADMIN, Roles.ORGANIZACION})
    public List<Organization> pendientes() {
        return service.getPendientes();
    }

    @PostMapping("/validar")
    @RequiereRol(Roles.ADMIN)
    public ResponseEntity<?> validar() {
        var validada = service.validarSiguiente();
        if (validada.isPresent()) {
            return ResponseEntity.ok(validada.get());
        }
        return ResponseEntity.status(404).body(new ApiMessage("No hay organizaciones pendientes"));
    }
}
