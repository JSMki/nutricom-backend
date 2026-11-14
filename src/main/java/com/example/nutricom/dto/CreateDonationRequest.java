package com.example.nutricom.dto;

import java.time.LocalDate;

public record CreateDonationRequest(
        LocalDate fechaDonacion,
        int cantidadTotal,
        String estado,
        String observaciones,
        int idComercio,
        int idOrganizacion) {
}