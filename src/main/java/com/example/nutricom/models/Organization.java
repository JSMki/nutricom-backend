package com.example.nutricom.models;

public class Organization {
    private int idOrganizacion;
    private String nombreOrg;
    private String tipoOrg;
    private String responsable;
    private String telefonoOrg;
    private String correoOrg;
    private String calleOrg;
    private String numeroOrg;
    private String estatusOrg;
    private int idColonia;
    private int idUsuario;

    public Organization(int idOrganizacion, String nombreOrg, String tipoOrg, String responsable,
                        String telefonoOrg, String correoOrg, String calleOrg, String numeroOrg,
                        String estatusOrg, int idColonia, int idUsuario) {
        this.idOrganizacion = idOrganizacion;
        this.nombreOrg = nombreOrg;
        this.tipoOrg = tipoOrg;
        this.responsable = responsable;
        this.telefonoOrg = telefonoOrg;
        this.correoOrg = correoOrg;
        this.calleOrg = calleOrg;
        this.numeroOrg = numeroOrg;
        this.estatusOrg = estatusOrg;
        this.idColonia = idColonia;
        this.idUsuario = idUsuario;
    }

    public int getIdOrganizacion() { return idOrganizacion; }
    public String getNombreOrg() { return nombreOrg; }
    public String getTipoOrg() { return tipoOrg; }
    public String getResponsable() { return responsable; }
    public String getTelefonoOrg() { return telefonoOrg; }
    public String getCorreoOrg() { return correoOrg; }
    public String getCalleOrg() { return calleOrg; }
    public String getNumeroOrg() { return numeroOrg; }
    public String getEstatusOrg() { return estatusOrg; }
    public int getIdColonia() { return idColonia; }
    public int getIdUsuario() { return idUsuario; }

    public void setEstatusOrg(String estatusOrg) { this.estatusOrg = estatusOrg; }
}
