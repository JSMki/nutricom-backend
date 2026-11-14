package com.example.nutricom.dto;

public record CreateCommerceRequest(
        String nombreComercio,
        String razonSocial,
        String tipoComercio,
        String telefonoComercio,
        String correoComercio,
        String calleComercio,
        String numeroComercio,
        int idColonia,
        int idUsuario) {
}
