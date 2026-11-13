package com.example.nutricom.models;

import java.time.LocalDate;

public class Request {
    private int idSolicitud;
    private LocalDate fechaSolicitud;
    private String motivo;
    private int numeroIntegrantes;
    private String prioridad;
    private String estado;

    public Request(int idSolicitud, LocalDate fechaSolicitud, String motivo,
                   int numeroIntegrantes, String prioridad, String estado) {
        this.idSolicitud = idSolicitud;
        this.fechaSolicitud = fechaSolicitud;
        this.motivo = motivo;
        this.numeroIntegrantes = numeroIntegrantes;
        this.prioridad = prioridad;
        this.estado = estado;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public LocalDate getFechaSolicitud() { return fechaSolicitud; }
    public String getMotivo() { return motivo; }
    public int getNumeroIntegrantes() { return numeroIntegrantes; }
    public String getPrioridad() { return prioridad; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}