package com.example.nutricom.dto;

public record CreateFoodRequest(
        String nombreAlimento,
        String descripcion,
        String unidadMedida,
        double caloriasPorPorcion,
        double proteinas,
        double carbohidratos,
        double grasas,
        String estado) {
}