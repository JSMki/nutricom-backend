package com.example.nutricom.models;

import java.time.LocalDateTime;

public class User {
    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String contrasenaHash;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private String estado;

    public User(int idUsuario, String nombreUsuario, String correo, String contrasenaHash, String telefono, String estado) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.fechaRegistro = LocalDateTime.now();
        this.estado = estado;
    }

    // Getters obligatorios para que Jackson convierta a JSON
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    public String getContrasenaHash() { return contrasenaHash; }
    public String getTelefono() { return telefono; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public String getEstado() { return estado; }
}