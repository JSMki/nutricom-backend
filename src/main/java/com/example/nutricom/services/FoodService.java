package com.example.nutricom.services;

import com.example.nutricom.dto.CreateFoodRequest;
import com.example.nutricom.models.Food;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodService {

    private final SinglyLinkedList<Food> foods = new SinglyLinkedList<>();
    private int siguienteId = 1;

    public FoodService() {
        crear(new CreateFoodRequest("Manzana", "Fruta fresca", "pieza", 95, 0.5, 25, 0.3, "activo"));
        crear(new CreateFoodRequest("Frijol negro", "Legumbre cocida", "taza", 227, 15, 41, 1, "activo"));
        crear(new CreateFoodRequest("Leche entera", "Lácteo pasteurizado", "litro", 150, 8, 12, 8, "activo"));
    }

    public List<Food> listar() {
        return foods.toList();
    }

    public Optional<Food> buscarPorId(int id) {
        return foods.find(f -> f.getIdAlimento() == id);
    }

    public Food crear(CreateFoodRequest request) {
        Food nuevo = new Food(
                siguienteId++,
                request.nombreAlimento(),
                request.descripcion(),
                request.unidadMedida(),
                request.caloriasPorPorcion(),
                request.proteinas(),
                request.carbohidratos(),
                request.grasas(),
                request.estado() == null || request.estado().isBlank() ? "activo" : request.estado());
        foods.insertLast(nuevo);
        return nuevo;
    }

    public Optional<Food> actualizar(int id, CreateFoodRequest request) {
        Optional<Food> encontrado = buscarPorId(id);
        encontrado.ifPresent(f -> {
            f.setNombreAlimento(request.nombreAlimento());
            f.setDescripcion(request.descripcion());
            f.setUnidadMedida(request.unidadMedida());
            f.setCaloriasPorPorcion(request.caloriasPorPorcion());
            f.setProteinas(request.proteinas());
            f.setCarbohidratos(request.carbohidratos());
            f.setGrasas(request.grasas());
            if (request.estado() != null && !request.estado().isBlank()) {
                f.setEstado(request.estado());
            }
        });
        return encontrado;
    }

    public boolean eliminar(int id) {
        return foods.removeIf(f -> f.getIdAlimento() == id);
    }
}