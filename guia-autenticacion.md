# Guía: Implementación de autenticación en el backend

Repositorio de referencia: [`https://github.com/AntonioGalvan/intro-spring`](https://github.com/AntonioGalvan/intro-spring)

Al terminar, el backend tendrá registro, login y `/api/auth/yo` con el mismo formato que el Fake API que revisamos en la clase de Plataformas abiertas 1, así que el front funciona igual cambiando solo `VITE_API_URL`.

**Nota:** En todos los archivos, cambiar `com.example.introspring` por el paquete del proyecto.

## Paso 1 · Dependencias

En `build.gradle`, dentro de `dependencies`, agregar:

```groovy
implementation 'org.springframework.boot:spring-boot-starter-validation'
implementation 'org.springframework.security:spring-security-crypto'
implementation 'com.auth0:java-jwt:4.4.0'
```

Recargar Gradle.

**Nota:** `spring-security-crypto` se usa para BCrypt (guardar contraseñas con hash) y `java-jwt` para crear y validar el token. Estas dos no aparecen en el panel de starters; se agregan a mano.

## Paso 2 · Variables de entorno

En `application.properties`, agregar:

```properties
server.port=${PORT:8080}

jwt.secreto=${JWT_SECRET}
jwt.expiracion-horas=${JWT_EXPIRACION_HORAS:8}

app.cors.origenes=${CORS_ORIGENES:http://localhost:*}
```

En `.env`, agregar:

```env
JWT_SECRET=una-cadena-larga-y-aleatoria-de-al-menos-32-caracteres
```

En `.env.example`, agregar:

```env
JWT_SECRET=una-cadena-larga-y-aleatoria-de-al-menos-32-caracteres

CORS_ORIGENES=https://su-front.vercel.app
```

**Nota:** `.env` no se sube a git. `CORS_ORIGENES` solo hace falta al desplegar, en local ya se acepta `localhost`.

## Paso 3 · Base de datos en Neon

En el SQL Editor de Neon, correr el script de la base del proyecto (Fundamentos de Base de Datos).

**Ajustar:** Para que el auth funcione, revisar que el script cumpla con esto:

- Sea de PostgreSQL. Si se hizo en otro motor, cambiar `AUTO_INCREMENT` o `IDENTITY(1,1)` por `GENERATED ALWAYS AS IDENTITY` y ajustar los tipos que no existan.
- La tabla de usuarios tenga: id autogenerado, nombre, correo único, contraseña, estado e id del rol (llave foránea a la tabla de roles).
- La columna de la contraseña sea de al menos `VARCHAR(100)`: el hash de BCrypt mide 60 caracteres.
- Las demás columnas `NOT NULL` de usuarios tengan un valor por defecto (`DEFAULT`), porque el registro solo llena nombre, correo, contraseña y rol.
- La tabla de roles tenga nombre del rol y estado, con los roles del proyecto ya insertados, en minúsculas y con estado `'activo'`.

Como referencia, así están en la plantilla (`sql/create-tables.sql`):

```sql
CREATE TABLE roles (
    id_rol INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_rol VARCHAR(40) NOT NULL UNIQUE,
    descripcion VARCHAR(150),
    estado VARCHAR(20) NOT NULL DEFAULT 'activo'
);

CREATE TABLE usuarios (
    id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_usuario VARCHAR(100) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    contrasena_hash VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'activo',
    id_rol INTEGER NOT NULL REFERENCES roles (id_rol)
);

INSERT INTO roles (nombre_rol, descripcion) VALUES
    ('admin', 'Administra la plataforma'),
    ('ciudadano', 'Levanta reportes'),
    ('tecnico', 'Atiende reportes');
```

**Nota:** Los nombres de tablas y columnas pueden ser distintos, se pueden ajustar en el paso 7.

## Paso 4 · Roles: `security/Roles.java`

Este archivo guarda en un solo lugar el nombre del rol de administrador y el rol que se asigna por defecto al registrarse.

Crear `security/Roles.java` y pegar:

```java
package com.example.introspring.security;

public final class Roles {

    public static final String ADMIN = "admin";
    public static final String POR_DEFECTO = "ciudadano";

    private Roles() {
    }
}
```

**Ajustar:** `ADMIN` y `POR_DEFECTO` con los nombres de roles del proyecto, igual que en la tabla de roles.

## Paso 5 · Modelo: `models/Usuario.java`

Crear `models/Usuario.java` y pegar:

```java
package com.example.introspring.models;

public record Usuario(
        int id,
        String nombre,
        String correo,
        String contrasenaHash,
        String estado,
        String rol) {
}
```

## Paso 6 · DTOs: carpeta `dto/`

Los DTOs definen qué entra y qué sale del API. Crear cada archivo y pegar.

`RegistroRequest.java`:

```java
package com.example.introspring.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistroRequest(
        @NotBlank(message = "El campo name es obligatorio") String name,
        @NotBlank(message = "El campo email es obligatorio")
        @Email(message = "El email no es válido") String email,
        @NotBlank(message = "El campo password es obligatorio")
        @Size(min = 6, message = "El password debe tener al menos 6 caracteres") String password,
        String role) {
}
```

`LoginRequest.java`:

```java
package com.example.introspring.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El campo email es obligatorio") String email,
        @NotBlank(message = "El campo password es obligatorio") String password,
        String role) {
}
```

`UsuarioResponse.java`:

```java
package com.example.introspring.dto;

import com.example.introspring.models.Usuario;

import java.util.List;

public record UsuarioResponse(int id, String name, String email, String role, List<String> roles) {

    public static UsuarioResponse de(Usuario usuario) {
        return new UsuarioResponse(
                usuario.id(),
                usuario.nombre(),
                usuario.correo(),
                usuario.rol(),
                List.of(usuario.rol()));
    }
}
```

`LoginResponse.java`:

```java
package com.example.introspring.dto;

public record LoginResponse(
        String token,
        UsuarioResponse user) {
}
```

`UsuarioToken.java`:

```java
package com.example.introspring.dto;

public record UsuarioToken(
        int id,
        String role) {
}
```

`Mensaje.java`:

```java
package com.example.introspring.dto;

public record Mensaje(String message) {
}
```

**Nota:** Los DTOs se copian tal cual. Los nombres en inglés (`name`, `email`, `role`, `user`, `message`) son los que espera el front, no hay que cambiarlos.

## Paso 7 · Repositorio: `repositories/UsuarioRepository.java`

Este archivo es el único que consulta la base para los usuarios. El `JOIN` trae el nombre del rol desde la tabla de roles.

Crear `repositories/UsuarioRepository.java` y pegar:

```java
package com.example.introspring.repositories;

import com.example.introspring.models.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final RowMapper<Usuario> MAPPER = (rs, fila) -> new Usuario(
            rs.getInt("id_usuario"),
            rs.getString("nombre_usuario"),
            rs.getString("correo"),
            rs.getString("contrasena_hash"),
            rs.getString("estado"),
            rs.getString("nombre_rol"));

    private static final String SELECT = """
            SELECT u.id_usuario, u.nombre_usuario, u.correo, u.contrasena_hash, u.estado, r.nombre_rol
            FROM usuarios u
            JOIN roles r ON r.id_rol = u.id_rol
            """;

    public Optional<Usuario> buscarPorId(int id) {
        return jdbc.query(SELECT + " WHERE u.id_usuario = ?", MAPPER, id).stream().findFirst();
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query(SELECT + " WHERE LOWER(u.correo) = LOWER(?)", MAPPER, correo).stream().findFirst();
    }

    public boolean existeCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE LOWER(correo) = LOWER(?)";
        return jdbc.queryForObject(sql, Integer.class, correo) > 0;
    }

    public boolean existeRol(String rol) {
        String sql = "SELECT COUNT(*) FROM roles WHERE nombre_rol = ? AND estado = 'activo'";
        return jdbc.queryForObject(sql, Integer.class, rol) > 0;
    }

    public Usuario crear(String nombre, String correo, String contrasenaHash, String rol) {
        String sql = """
                INSERT INTO usuarios (nombre_usuario, correo, contrasena_hash, id_rol)
                VALUES (?, ?, ?, (SELECT id_rol FROM roles WHERE nombre_rol = ?))
                RETURNING id_usuario
                """;
        Integer id = jdbc.queryForObject(sql, Integer.class, nombre, correo, contrasenaHash, rol);
        return buscarPorId(id).orElseThrow();
    }
}
```

**Ajustar:** Los nombres de tablas y columnas por los del proyecto, en todo el archivo (`MAPPER`, `SELECT`, `existeCorreo`, `existeRol` y `crear`).

**Nota:** Si el estado no se guarda como `'activo'` (por ejemplo `'Activo'`, `'A'` o un booleano), cambiar también esa condición en `existeRol` y en `AuthService` (Paso 9).

**Nota:** Los valores siempre se pasan con `?`, nunca concatenados en el texto del SQL.

## Paso 8 · Token: `services/JwtService.java`

Este archivo crea el token en el login y lo valida en cada petición.

Crear `services/JwtService.java` y pegar:

```java
package com.example.introspring.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.introspring.dto.UsuarioToken;
import com.example.introspring.models.Usuario;
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

    public String crearToken(Usuario usuario) {
        Instant ahora = Instant.now();
        return JWT.create()
                .withSubject(String.valueOf(usuario.id()))
                .withClaim("name", usuario.nombre())
                .withClaim("role", usuario.rol())
                .withIssuedAt(ahora)
                .withExpiresAt(ahora.plus(horasDeVida, ChronoUnit.HOURS))
                .sign(algoritmo);
    }

    public UsuarioToken leerToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Falta el token en el header Authorization");
        }
        try {
            DecodedJWT jwt = verificador.verify(authorization.substring("Bearer ".length()));
            return new UsuarioToken(Integer.parseInt(jwt.getSubject()), jwt.getClaim("role").asString());
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o vencido");
        }
    }
}
```

## Paso 9 · Lógica del auth: `services/AuthService.java`

Este archivo tiene las reglas: registrar, iniciar sesión y consultar el usuario del token.

Crear `services/AuthService.java` y pegar:

```java
package com.example.introspring.services;

import com.example.introspring.dto.LoginRequest;
import com.example.introspring.dto.LoginResponse;
import com.example.introspring.dto.RegistroRequest;
import com.example.introspring.dto.UsuarioResponse;
import com.example.introspring.models.Usuario;
import com.example.introspring.repositories.UsuarioRepository;
import com.example.introspring.security.Roles;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UsuarioRepository usuarios;
    private final JwtService jwt;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarios, JwtService jwt) {
        this.usuarios = usuarios;
        this.jwt = jwt;
    }

    public UsuarioResponse registrar(RegistroRequest request) {
        String nombre = request.name().trim();
        String correo = request.email().trim().toLowerCase();
        String password = request.password();
        String rol = request.role() == null || request.role().isBlank()
                ? Roles.POR_DEFECTO
                : request.role().trim().toLowerCase();

        if (rol.equals(Roles.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se puede registrar una cuenta de admin");
        }
        if (!usuarios.existeRol(rol)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol " + rol + " no existe");
        }
        if (usuarios.existeCorreo(correo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una cuenta con ese email");
        }

        Usuario nuevo = usuarios.crear(nombre, correo, encoder.encode(password), rol);
        return UsuarioResponse.de(nuevo);
    }

    public LoginResponse login(LoginRequest request) {
        String correo = request.email().trim();
        String password = request.password();

        Usuario usuario = usuarios.buscarPorCorreo(correo)
                .filter(u -> encoder.matches(password, u.contrasenaHash()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Email o password incorrectos"));

        if (!usuario.estado().equalsIgnoreCase("activo")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está inactiva");
        }

        String rolPedido = request.role();
        if (rolPedido != null && !rolPedido.isBlank() && !rolPedido.trim().equalsIgnoreCase(usuario.rol())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Esta cuenta no tiene el rol " + rolPedido.trim());
        }

        return new LoginResponse(jwt.crearToken(usuario), UsuarioResponse.de(usuario));
    }

    public UsuarioResponse yo(int id) {
        return usuarios.buscarPorId(id)
                .map(usuario -> UsuarioResponse.de(usuario))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "La cuenta del token ya no existe"));
    }
}
```

**Nota:** Si se manda `role` en el login, debe ser el rol que el usuario tiene en la base, si no se manda, se usa el de la base. El token siempre lleva el rol de la base.

**Ajustar:** Si el estado activo no se guarda como `'activo'`, cambiarlo en la línea de `usuario.estado()`.

## Paso 10 · Errores: `errors/ManejadorErrores.java`

Este archivo convierte cualquier error del API en una respuesta `{ "message": "..." }`, que es la que muestra el front.

Crear `errors/ManejadorErrores.java` y pegar:

```java
package com.example.introspring.errors;

import com.example.introspring.dto.Mensaje;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Mensaje> manejar(ResponseStatusException e) {
        return ResponseEntity.status(e.getStatusCode()).body(new Mensaje(e.getReason()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Mensaje> jsonInvalido() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Mensaje("El cuerpo de la petición no es un JSON válido"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Mensaje> datosInvalidos(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(new Mensaje(mensaje));
    }
}
```

**Nota:** Este archivo se aplica solo a todos los controllers.

## Paso 11 · Protección de rutas: carpeta `security/`

Estos tres archivos hacen que todas las rutas `/api/**` pidan token (salvo login, registro y health), revisan el rol y configuran el CORS.

`RequiereRol.java`:

```java
package com.example.introspring.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiereRol {
    String[] value();
}
```

`AuthInterceptor.java`:

```java
package com.example.introspring.security;

import com.example.introspring.dto.UsuarioToken;
import com.example.introspring.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtService jwt;

    public AuthInterceptor(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod metodo)) {
            return true;
        }

        UsuarioToken usuario = jwt.leerToken(request.getHeader("Authorization"));
        request.setAttribute("usuario", usuario);

        RequiereRol requiere = metodo.getMethodAnnotation(RequiereRol.class);
        if (requiere != null && !Arrays.asList(requiere.value()).contains(usuario.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para esta acción");
        }
        return true;
    }
}
```

`WebConfig.java`:

```java
package com.example.introspring.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final String[] origenes;

    public WebConfig(AuthInterceptor authInterceptor, @Value("${app.cors.origenes}") String[] origenes) {
        this.authInterceptor = authInterceptor;
        this.origenes = origenes;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login", "/api/auth/registro", "/api/health", "/api/health/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(origenes)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
```

**Ajustar:** Si alguna ruta pública del proyecto no empieza con `/api/auth/` ni `/api/health`, agregarla en `excludePathPatterns`.

## Paso 12 · Endpoints: `controllers/AuthController.java`

Crear `controllers/AuthController.java` y pegar:

```java
package com.example.introspring.controllers;

import com.example.introspring.dto.LoginRequest;
import com.example.introspring.dto.LoginResponse;
import com.example.introspring.dto.RegistroRequest;
import com.example.introspring.dto.UsuarioResponse;
import com.example.introspring.dto.UsuarioToken;
import com.example.introspring.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse registrar(@Valid @RequestBody RegistroRequest request) {
        return authService.registrar(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/yo")
    public UsuarioResponse yo(@RequestAttribute("usuario") UsuarioToken usuario) {
        return authService.yo(usuario.id());
    }
}
```

## Paso 13 · Proteger los endpoints del proyecto

Con el Paso 11, todos los endpoints `/api/**` ya piden token. Para que un endpoint solo lo use un rol, agregar `@RequiereRol`:

```java
@PostMapping
@RequiereRol(Roles.ADMIN)
public Artist crear(@RequestBody CreateArtistRequest request) {
    return artistService.crear(request);
}
```

Varios roles:

```java
@RequiereRol({Roles.ADMIN, "tecnico"})
```

**Ajustar:** Agregar `@RequiereRol` en los endpoints que el proyecto restringe por rol, con los roles de su `permisos.js` del front.

## Paso 14 · Crear el usuario admin

El registro no permite crear admins. Para el primero:

1. Registrar al usuario normal en Postman (Paso 15).
2. En el SQL Editor de Neon, correr (con el correo de ese usuario):

```sql
UPDATE usuarios
SET id_rol = (SELECT id_rol FROM roles WHERE nombre_rol = 'admin')
WHERE correo = 'admin@correo.com';
```

3. Volver a hacer login para obtener un token con el rol nuevo.

**Ajustar:** Nombres de tablas, columnas y del rol admin del proyecto.

## Paso 15 · Probar

Registrar un usuario:

```http
POST http://localhost:8080/api/auth/registro
Content-Type: application/json

{ "name": "Ana Torres", "email": "ana@correo.com", "password": "123456", "role": "tecnico" }
```

Iniciar sesión:

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{ "email": "ana@correo.com", "password": "123456" }
```

Consultar el usuario del token:

```http
GET http://localhost:8080/api/auth/yo
Authorization: Bearer <token>
```

Para conectar el front, en su `.env.local` cambiar a:

```env
VITE_API_URL=http://localhost:8080
```

y reiniciar `npm run dev`.

**Nota:** Los usuarios del API temporal no existen en esta base; hay que registrarlos de nuevo.