package com.example.nutricom.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.nutricom.dto.UsuarioToken;
import com.example.nutricom.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    private final Algorithm algoritmo;
    private final JWTVerifier verificador;
    private final long horasDeVida;

    public JwtService(
            @Value("${jwt.secreto}") String secreto,
            @Value("${jwt.expiracion-horas}") long horasDeVida) {
        this.algoritmo = Algorithm.HMAC256(secreto);
        this.verificador = JWT.require(algoritmo).build();
        this.horasDeVida = horasDeVida;
    }

    public String crearToken(User usuario) {
        Instant ahora = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(usuario.getIdUsuario()))
                .withClaim("name", usuario.getNombreUsuario())
                .withClaim("role", usuario.getRol())
                .withIssuedAt(ahora)
                .withExpiresAt(ahora.plus(horasDeVida, ChronoUnit.HOURS))
                .sign(algoritmo);
    }

    public UsuarioToken leerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Falta el token en el header Authorization");
        }
        try {
            DecodedJWT jwt = verificador.verify(authorization.substring("Bearer ".length()));
            return new UsuarioToken(Integer.parseInt(jwt.getSubject()), jwt.getClaim("role").asString());
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o vencido");
        }
    }
}
