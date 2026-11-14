package com.example.nutricom.services;

import com.example.nutricom.dto.CreateRequestRequest;
import com.example.nutricom.models.Request;
import com.example.nutricom.structures.Queue;
import com.example.nutricom.structures.SinglyLinkedList;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    // Pending requests: served in arrival order
    private final Queue<Request> pending = new Queue<>();
    // History of already served requests
    private final SinglyLinkedList<Request> served = new SinglyLinkedList<>();
    private int nextId = 1;

    public RequestService() {
        create(new CreateRequestRequest(LocalDate.of(2026, 11, 10), "Familia sin despensa esta semana", 5, "alta"));
        create(new CreateRequestRequest(LocalDate.of(2026, 11, 11), "Apoyo para comedor comunitario", 30, "media"));
        create(new CreateRequestRequest(LocalDate.of(2026, 11, 12), "Frutas y verduras para niños", 4, "baja"));
    }

    public List<Request> listPending() {
        return pending.toList();
    }

    public List<Request> listServed() {
        return served.toList();
    }

    public Optional<Request> findById(int id) {
        Optional<Request> inQueue = pending.toList().stream()
                .filter(r -> r.getIdSolicitud() == id)
                .findFirst();
        if (inQueue.isPresent()) {
            return inQueue;
        }
        return served.find(r -> r.getIdSolicitud() == id);
    }

    public Request create(CreateRequestRequest dto) {
        Request request = new Request(
                nextId++,
                dto.fechaSolicitud() == null ? LocalDate.now() : dto.fechaSolicitud(),
                dto.motivo(),
                dto.numeroIntegrantes(),
                dto.prioridad() == null || dto.prioridad().isBlank() ? "media" : dto.prioridad(),
                "pendiente");
        pending.enqueue(request);
        return request;
    }

    // Serves the request that has been waiting the longest
    public Optional<Request> serveNext() {
        Optional<Request> next = pending.dequeue();
        next.ifPresent(r -> {
            r.setEstado("atendida");
            served.insertLast(r);
        });
        return next;
    }

    // Updates the data of an existing request (pending or served)
    public Optional<Request> update(int id, CreateRequestRequest dto) {
        Optional<Request> found = findById(id);
        found.ifPresent(r -> {
            if (dto.fechaSolicitud() != null) r.setFechaSolicitud(dto.fechaSolicitud());
            r.setMotivo(dto.motivo());
            r.setNumeroIntegrantes(dto.numeroIntegrantes());
            if (dto.prioridad() != null && !dto.prioridad().isBlank()) r.setPrioridad(dto.prioridad());
        });
        return found;
    }

    // Removes the request from the queue or from the history
    public boolean delete(int id) {
        return pending.removeIf(r -> r.getIdSolicitud() == id)
                || served.removeIf(r -> r.getIdSolicitud() == id);
    }
}