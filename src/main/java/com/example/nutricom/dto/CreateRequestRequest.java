package com.example.nutricom.dto;

import java.time.LocalDate;

public record CreateRequestRequest(
        LocalDate fechaSolicitud,
        String motivo,
        int numeroIntegrantes,
        String prioridad) {
}