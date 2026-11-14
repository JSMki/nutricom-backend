package com.example.nutricom.models;

import java.time.LocalDate;

public class Donations {
    private int idDonacion;
    private LocalDate fechaDonacion;
    private int cantidadTotal;
    private String estado;
    private String observaciones;
    private int idComercio;
    private int idOrganizacion;

    public Donations(int idDonacion, LocalDate fechaDonacion, int cantidadTotal, String estado,
                    String observaciones, int idComercio, int idOrganizacion) {
        this.idDonacion = idDonacion;
        this.fechaDonacion = fechaDonacion;
        this.cantidadTotal = cantidadTotal;
        this.estado = estado;
        this.observaciones = observaciones;
        this.idComercio = idComercio;
        this.idOrganizacion = idOrganizacion;
    }

    public int getIdDonacion() { return idDonacion; }
    public LocalDate getFechaDonacion() { return fechaDonacion; }
    public int getCantidadTotal() { return cantidadTotal; }
    public String getEstado() { return estado; }
    public String getObservaciones() { return observaciones; }
    public int getIdComercio() { return idComercio; }
    public int getIdOrganizacion() { return idOrganizacion; }

    public void setFechaDonacion(LocalDate fechaDonacion) { this.fechaDonacion = fechaDonacion; }
    public void setCantidadTotal(int cantidadTotal) { this.cantidadTotal = cantidadTotal; }
    public void setEstado(String estado) { this.estado = estado; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public void setIdOrganizacion(int idOrganizacion) { this.idOrganizacion = idOrganizacion; }
}
