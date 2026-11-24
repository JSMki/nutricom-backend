package com.example.nutricom.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class User {
    private int idUsuario;
    private String nombreUsuario;
    private String correo;
    private String contrasenaHash;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private String estado;
    private String rol;

    public User(int idUsuario, String nombreUsuario, String correo, String contrasenaHash, String telefono, String estado) {
        this(idUsuario, nombreUsuario, correo, contrasenaHash, telefono, LocalDateTime.now(), estado, null);
    }

    public User(
            int idUsuario,
            String nombreUsuario,
            String correo,
            String contrasenaHash,
            String telefono,
            LocalDateTime fechaRegistro,
            String estado,
            String rol) {
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.correo = correo;
        this.contrasenaHash = contrasenaHash;
        this.telefono = telefono;
        this.fechaRegistro = fechaRegistro;
        this.estado = estado;
        this.rol = rol;
    }

    // Getters obligatorios para que Jackson convierta a JSON
    public int getIdUsuario() { return idUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public String getCorreo() { return correo; }
    @JsonIgnore
    public String getContrasenaHash() { return contrasenaHash; }
    public String getTelefono() { return telefono; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public String getEstado() { return estado; }
    @JsonIgnore
    public String getRol() { return rol; }
}