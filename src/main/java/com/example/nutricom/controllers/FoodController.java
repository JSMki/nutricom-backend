package com.example.nutricom.controllers;

import com.example.nutricom.dto.CreateFoodRequest;
import com.example.nutricom.models.Food;
import com.example.nutricom.services.FoodService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/foods")
public class FoodController {

    private final FoodService service;

    public FoodController(FoodService service) {
        this.service = service;
    }

    @GetMapping
    public List<Food> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable int id) {
        return service.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Food " + id + " no encontrado")));
    }

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody CreateFoodRequest request) {
        if (request.nombreAlimento() == null || request.nombreAlimento().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El campo 'nombreAlimento' es obligatorio"));
        }
        return ResponseEntity.status(201).body(service.crear(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable int id, @RequestBody CreateFoodRequest request) {
        if (request.nombreAlimento() == null || request.nombreAlimento().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", "El campo 'nombreAlimento' es obligatorio"));
        }
        return service.actualizar(id, request)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("message", "Food " + id + " no encontrado")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable int id) {
        if (service.eliminar(id)) {
            return ResponseEntity.ok(Map.of("message", "Food " + id + " fue eliminado"));
        }
        return ResponseEntity.status(404)
                .body(Map.of("message", "Food " + id + " no encontrado"));
    }
}
