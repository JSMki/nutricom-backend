package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateRequestRequest;
import com.example.nutricom.models.Request;
import com.example.nutricom.services.RequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<Request> findById(@PathVariable int id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Request> create(@RequestBody CreateRequestRequest dto) {
        if (dto.motivo() == null || dto.motivo().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @PostMapping("/serve")
    public ResponseEntity<Request> serveNext() {
        return service.serveNext()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Request> update(@PathVariable int id, @RequestBody CreateRequestRequest dto) {
        if (dto.motivo() == null || dto.motivo().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return service.update(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable int id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}