package com.example.nutricom.services;

import com.example.nutricom.dto.CreateOrganizationRequest;
import com.example.nutricom.models.Organization;
import com.example.nutricom.structures.Queue;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizationService {
    private final SinglyLinkedList<Organization> activas = new SinglyLinkedList<>();
    private final Queue<Organization> pendientes = new Queue<>();
    private int nextId = 1;

    public OrganizationService() {
        activas.insertLast(new Organization(nextId++, "Banco de Alimentos Esperanza", "Banco de alimentos",
                "Laura Méndez", "2221112233", "contacto@esperanza.org", "Reforma", "120", "activa", 1, 2));
        activas.insertLast(new Organization(nextId++, "Centro Comunitario Hortensia", "Centro comunitario",
                "Pedro Ruiz", "2224445566", "hortensia@centro.org", "5 de Mayo", "45", "activa", 2, 2));
        activas.insertLast(new Organization(nextId++, "Asociación Manos Solidarias", "Asociación civil",
                "Sofía Torres", "2227778899", "manos@solidarias.org", "Juárez", "300", "activa", 3, 2));

        // 2 en espera de validación, para que la cola no arranque vacía
        pendientes.enqueue(new Organization(nextId++, "Comedor San José", "Comedor comunitario",
                "Raúl Pérez", "2223334455", "comedor@sanjose.org", "Hidalgo", "18", "pendiente", 4, 2));
        pendientes.enqueue(new Organization(nextId++, "Red Vecinal Norte", "Asociación civil",
                "Elena Cruz", "2226667788", "red@vecinalnorte.org", "Morelos", "77", "pendiente", 5, 2));
    }

    public List<Organization> getAll() {
        return activas.toList();
    }

    public Optional<Organization> getById(int id) {
        return activas.find(o -> o.getIdOrganizacion() == id);
    }

    public Organization create(CreateOrganizationRequest request) {
        Organization nueva = new Organization(nextId++, request.nombreOrg(), request.tipoOrg(),
                request.responsable(), request.telefonoOrg(), request.correoOrg(), request.calleOrg(),
                request.numeroOrg(), "pendiente", request.idColonia(), request.idUsuario());
        pendientes.enqueue(nueva);
        return nueva;
    }

    public List<Organization> getPendientes() {
        return pendientes.toList();
    }

    public Optional<Organization> validarSiguiente() {
        Optional<Organization> siguiente = pendientes.dequeue();
        siguiente.ifPresent(org -> {
            org.setEstatusOrg("activa");
            activas.insertLast(org);
        });
        return siguiente;
    }
}