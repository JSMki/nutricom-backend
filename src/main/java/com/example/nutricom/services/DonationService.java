package com.example.nutricom.services;

import com.example.nutricom.dto.CreateDonationRequest;
import com.example.nutricom.models.Donations;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private final SinglyLinkedList<Donations> donations = new SinglyLinkedList<>();
    private int nextId = 1;

    public DonationService() {
        create(new CreateDonationRequest(LocalDate.of(2026, 11, 3), 20, "entregada",
                "Arroz y frijol en buen estado", 1, 1));
        create(new CreateDonationRequest(LocalDate.of(2026, 11, 10), 12, "pendiente",
                "", 1, 2));
        create(new CreateDonationRequest(LocalDate.of(2026, 11, 5), 8, "entregada",
                "Verdura de temporada", 2, 3));
    }

    public List<Donations> findAll() {
        return donations.toList();
    }

    public Optional<Donations> findById(int id) {
        return donations.find(d -> d.getIdDonacion() == id);
    }

    // Donaciones que recibió una organización
    public List<Donations> findByOrganization(int idOrganizacion) {
        return donations.toList().stream()
                .filter(d -> d.getIdOrganizacion() == idOrganizacion)
                .toList();
    }

    public Donations create(CreateDonationRequest dto) {
        Donations donation = new Donations(
                nextId++,
                dto.fechaDonacion() == null ? LocalDate.now() : dto.fechaDonacion(),
                dto.cantidadTotal(),
                isBlank(dto.estado()) ? "pendiente" : dto.estado(),
                dto.observaciones() == null ? "" : dto.observaciones(),
                dto.idComercio(),
                dto.idOrganizacion());
        donations.insertLast(donation);
        return donation;
    }

    public Optional<Donations> update(int id, CreateDonationRequest dto) {
        Optional<Donations> found = findById(id);
        found.ifPresent(d -> {
            if (dto.fechaDonacion() != null) d.setFechaDonacion(dto.fechaDonacion());
            d.setCantidadTotal(dto.cantidadTotal());
            if (!isBlank(dto.estado())) d.setEstado(dto.estado());
            d.setObservaciones(dto.observaciones() == null ? "" : dto.observaciones());
            d.setIdOrganizacion(dto.idOrganizacion());
        });
        return found;
    }

    public boolean delete(int id) {
        return donations.removeIf(d -> d.getIdDonacion() == id);
    }

    private boolean isBlank(String text) {
        return text == null || text.isBlank();
    }
}
