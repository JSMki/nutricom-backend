package com.example.nutricom.services;

import com.example.nutricom.dto.CreateCommerceRequest;
import com.example.nutricom.models.Commerce;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommerceService {

    private final SinglyLinkedList<Commerce> commerces = new SinglyLinkedList<>();
    private int nextId = 1;

    public CommerceService() {
        create(new CreateCommerceRequest("Mercado Hidalgo", "Mercado Municipal Hidalgo A.C.", "Mercado",
                "2221234567", "contacto@mercadohidalgo.mx", "Av. Reforma", "210", 1, 3));
        create(new CreateCommerceRequest("Panadería La Espiga", "La Espiga S.A. de C.V.", "Panadería",
                "2229876543", "ventas@laespiga.mx", "5 de Mayo", "18", 2, 3));
        create(new CreateCommerceRequest("Verdulería Don Chuy", "Jesús Morales Pérez", "Productor local",
                "2225558899", "donchuy@correo.com", "Juárez", "75", 3, 3));
    }

    public List<Commerce> findAll() {
        return commerces.toList();
    }

    public Optional<Commerce> findById(int id) {
        return commerces.find(c -> c.getIdComercio() == id);
    }

    public Commerce create(CreateCommerceRequest dto) {
        Commerce commerce = new Commerce(
                nextId++,
                dto.nombreComercio(),
                dto.razonSocial(),
                dto.tipoComercio(),
                dto.telefonoComercio(),
                dto.correoComercio(),
                dto.calleComercio(),
                dto.numeroComercio(),
                dto.idColonia(),
                dto.idUsuario());
        commerces.insertLast(commerce);
        return commerce;
    }

    public Optional<Commerce> update(int id, CreateCommerceRequest dto) {
        Optional<Commerce> found = findById(id);
        found.ifPresent(c -> {
            c.setNombreComercio(dto.nombreComercio());
            c.setRazonSocial(dto.razonSocial());
            c.setTipoComercio(dto.tipoComercio());
            c.setTelefonoComercio(dto.telefonoComercio());
            c.setCorreoComercio(dto.correoComercio());
            c.setCalleComercio(dto.calleComercio());
            c.setNumeroComercio(dto.numeroComercio());
            c.setIdColonia(dto.idColonia());
        });
        return found;
    }

    public boolean delete(int id) {
        return commerces.removeIf(c -> c.getIdComercio() == id);
    }
}
