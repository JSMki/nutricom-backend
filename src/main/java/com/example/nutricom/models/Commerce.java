package com.example.nutricom.models;

public class Commerce {
    private int idComercio;
    private String nombreComercio;
    private String razonSocial;
    private String tipoComercio;
    private String telefonoComercio;
    private String correoComercio;
    private String calleComercio;
    private String numeroComercio;
    private int idColonia;
    private int idUsuario;

    public Commerce(int idComercio, String nombreComercio, String razonSocial, String tipoComercio,
                    String telefonoComercio, String correoComercio, String calleComercio,
                    String numeroComercio, int idColonia, int idUsuario) {
        this.idComercio = idComercio;
        this.nombreComercio = nombreComercio;
        this.razonSocial = razonSocial;
        this.tipoComercio = tipoComercio;
        this.telefonoComercio = telefonoComercio;
        this.correoComercio = correoComercio;
        this.calleComercio = calleComercio;
        this.numeroComercio = numeroComercio;
        this.idColonia = idColonia;
        this.idUsuario = idUsuario;
    }

    public int getIdComercio() { return idComercio; }
    public String getNombreComercio() { return nombreComercio; }
    public String getRazonSocial() { return razonSocial; }
    public String getTipoComercio() { return tipoComercio; }
    public String getTelefonoComercio() { return telefonoComercio; }
    public String getCorreoComercio() { return correoComercio; }
    public String getCalleComercio() { return calleComercio; }
    public String getNumeroComercio() { return numeroComercio; }
    public int getIdColonia() { return idColonia; }
    public int getIdUsuario() { return idUsuario; }

    public void setNombreComercio(String nombreComercio) { this.nombreComercio = nombreComercio; }
    public void setRazonSocial(String razonSocial) { this.razonSocial = razonSocial; }
    public void setTipoComercio(String tipoComercio) { this.tipoComercio = tipoComercio; }
    public void setTelefonoComercio(String telefonoComercio) { this.telefonoComercio = telefonoComercio; }
    public void setCorreoComercio(String correoComercio) { this.correoComercio = correoComercio; }
    public void setCalleComercio(String calleComercio) { this.calleComercio = calleComercio; }
    public void setNumeroComercio(String numeroComercio) { this.numeroComercio = numeroComercio; }
    public void setIdColonia(int idColonia) { this.idColonia = idColonia; }
}
