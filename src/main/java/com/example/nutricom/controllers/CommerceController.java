package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateCommerceRequest;
import com.example.nutricom.models.Commerce;
import com.example.nutricom.security.RequiereRol;
import com.example.nutricom.security.Roles;
import com.example.nutricom.services.CommerceService;
import com.example.nutricom.services.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/commerces")
public class CommerceController {

    private final CommerceService service;
    private final DonationService donationService;

    public CommerceController(CommerceService service, DonationService donationService) {
        this.service = service;
        this.donationService = donationService;
    }

    @GetMapping
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public List<Commerce> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public ResponseEntity<?> findById(@PathVariable int id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(notFound(id));
    }

    // Donaciones que ha hecho este comercio
    @GetMapping("/{id}/donations")
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public ResponseEntity<?> findDonations(@PathVariable int id) {
        if (service.findById(id).isEmpty()) {
            return notFound(id);
        }
        return ResponseEntity.ok(donationService.findByCommerce(id));
    }

    @PostMapping
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public ResponseEntity<?> create(@RequestBody CreateCommerceRequest dto) {
        String error = validate(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{id}")
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CreateCommerceRequest dto) {
        String error = validate(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return service.update(id, dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(notFound(id));
    }

    @DeleteMapping("/{id}")
    @RequiereRol({Roles.ADMIN, Roles.COMERCIO})
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (service.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Commerce " + id + " was deleted"));
        }
        return notFound(id);
    }

    private ResponseEntity<?> notFound(int id) {
        return ResponseEntity.status(404).body(Map.of("message", "Commerce " + id + " not found"));
    }

    // Revisa los campos obligatorios; regresa el mensaje de error o null si todo está bien
    private String validate(CreateCommerceRequest dto) {
        if (dto.nombreComercio() == null || dto.nombreComercio().isBlank()) {
            return "The field 'nombreComercio' is required";
        }
        if (dto.tipoComercio() == null || dto.tipoComercio().isBlank()) {
            return "The field 'tipoComercio' is required";
        }
        if (dto.telefonoComercio() == null || !dto.telefonoComercio().matches("\\d{10}")) {
            return "The field 'telefonoComercio' must have 10 digits";
        }
        return null;
    }
}
