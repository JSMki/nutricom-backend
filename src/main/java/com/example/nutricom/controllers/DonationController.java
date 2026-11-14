package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateDonationRequest;
import com.example.nutricom.models.Donations;
import com.example.nutricom.services.DonationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    private final DonationService service;

    public DonationController(DonationService service) {
        this.service = service;
    }

    @GetMapping
    public List<Donations> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Donation " + id + " not found")));
    }

    @GetMapping("/organization/{idOrganizacion}")
    public List<Donations> findByOrganization(@PathVariable int idOrganizacion) {
        return service.findByOrganization(idOrganizacion);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateDonationRequest dto) {
        String error = validate(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CreateDonationRequest dto) {
        String error = validate(dto);
        if (error != null) {
            return ResponseEntity.badRequest().body(Map.of("message", error));
        }
        return service.update(id, dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Donation " + id + " not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (service.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Donation " + id + " was deleted"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Donation " + id + " not found"));
    }

    // Revisa los datos obligatorios; regresa el mensaje de error o null si todo está bien
    private String validate(CreateDonationRequest dto) {
        if (dto.cantidadTotal() <= 0) {
            return "The field 'cantidadTotal' must be greater than 0";
        }
        if (dto.idComercio() <= 0) {
            return "The field 'idComercio' is required";
        }
        if (dto.idOrganizacion() <= 0) {
            return "The field 'idOrganizacion' is required";
        }
        return null;
    }
}
