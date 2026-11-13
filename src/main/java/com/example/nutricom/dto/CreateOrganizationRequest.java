package com.example.nutricom.dto;

public record CreateOrganizationRequest(
        String nombreOrg,
        String tipoOrg,
        String responsable,
        String telefonoOrg,
        String correoOrg,
        String calleOrg,
        String numeroOrg,
        int idColonia,
        int idUsuario
) {}