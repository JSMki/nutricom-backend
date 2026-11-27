package com.example.nutricom.services;

import com.example.nutricom.dto.LoginRequest;
import com.example.nutricom.dto.LoginResponse;
import com.example.nutricom.dto.RegistroRequest;
import com.example.nutricom.dto.UsuarioResponse;
import com.example.nutricom.models.User;
import com.example.nutricom.repositories.UsuarioRepository;
import com.example.nutricom.security.Roles;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Service
public class AuthService {

    private static final Set<String> ROLES_REGISTRABLES = Set.of(
            Roles.COMERCIO, Roles.ORGANIZACION, Roles.BENEFICIARIO);

    private final UsuarioRepository usuarios;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarios, JwtService jwt) {
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String nombre = request.name().trim();
        String correo = request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();
        String rol = request.role() == null || request.role().isBlank()
                ? Roles.POR_DEFECTO
                : request.role().trim().toLowerCase(Locale.ROOT);

        if (!ROLES_REGISTRABLES.contains(rol)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede registrar una cuenta con ese rol");
        }
        if (!usuarios.existeRol(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol " + rol + " no existe");
        }
        if (usuarios.existeCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        User nuevo = usuarios.crear(nombre, correo, encoder.encode(password), rol);
        return UsuarioResponse.de(nuevo);
    }

    public LoginResponse login(LoginRequest request) {
        String correo = request.email().trim().toLowerCase(Locale.ROOT);
        String password = request.password();

        User usuario = usuarios.buscarPorCorreo(correo)
                .filter(u -> encoder.matches(password, u.getContrasenaHash()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email o password incorrectos"));

        if (!usuario.getEstado().equalsIgnoreCase("activo")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está inactiva");
        }

        String rolPedido = request.role();
        if (rolPedido != null && !rolPedido.isBlank()
                && !rolPedido.trim().equalsIgnoreCase(usuario.getRol())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Esta cuenta no tiene el rol " + rolPedido.trim());
        }

        return new LoginResponse(jwt.crearToken(usuario), UsuarioResponse.de(usuario));
    }

    public UsuarioResponse yo(int id) {
        return usuarios.buscarPorId(id)
                .map(UsuarioResponse::de)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "La cuenta del token ya no existe"));
    }
}
