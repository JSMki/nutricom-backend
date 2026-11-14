package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateRequestRequest;
import com.example.nutricom.models.Request;
import com.example.nutricom.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestService service;

    public RequestController(RequestService service) {
        this.service = service;
    }

    @GetMapping
    public List<Request> listPending() {
        return service.listPending();
    }

    @GetMapping("/served")
    public List<Request> listServed() {
        return service.listServed();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable int id) {
        return service.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Request " + id + " not found")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateRequestRequest dto) {
        if (dto.motivo() == null || dto.motivo().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "The field 'motivo' is required"));
        }
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PostMapping("/serve")
    public ResponseEntity<?> serveNext() {
        return service.serveNext()
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "There are no pending requests")));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable int id, @RequestBody CreateRequestRequest dto) {
        if (dto.motivo() == null || dto.motivo().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "The field 'motivo' is required"));
        }
        return service.update(id, dto)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Request " + id + " not found")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if (service.delete(id)) {
            return ResponseEntity.ok(Map.of("message", "Request " + id + " was deleted"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Request " + id + " not found"));
    }
}