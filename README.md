# NutriComunidad

Plataforma que conecta mercados, comercios, productores locales, organizaciones sociales y familias para aprovechar alimentos aptos para consumo y promover una alimentación saludable.

Este repositorio contiene el **backend (API REST)** desarrollado con Spring Boot y Gradle. Los módulos implementados guardan sus datos **en memoria** usando estructuras de datos propias. La conexión configurada es a una base de datos **PostgreSQL alojada en Neon** y se utiliza para comprobar conectividad; los registros de los módulos no se guardan en la base de datos.

## Integrantes

- Jaime Iván Sánchez Romero - sanchezrjaimei@gmail.com
- Carolina Reyes Pitones - ___

## Tecnologías

- Java 17
- Spring Boot 4.1.2
- Gradle
- PostgreSQL en Neon (comprobación de conexión)
- Postman (para probar los endpoints)

## Requisitos

- JDK 17
- Una base de datos PostgreSQL en Neon y sus datos de conexión
- Postman

## Configuración

1. Clona el repositorio.
2. Copia `.env.example` a `.env`.
3. En Neon, copia los datos de conexión de tu proyecto y configúralos en `.env`. Reemplaza los valores de ejemplo por los tuyos. La URL JDBC debe incluir `sslmode=require`:

   ```properties
   DB_URL=jdbc:postgresql://<host-neon>/<base-de-datos>?sslmode=require
   DB_USER=<usuario-de-neon>
   DB_PASSWORD=<contraseña-de-neon>
   JWT_SECRET=escribe-una-clave-larga-de-al-menos-32-caracteres
   FRONTEND_URL=
   ```

   `FRONTEND_URL` es opcional. El archivo `.env` está ignorado por Git; no subas credenciales reales al repositorio. No necesitas instalar PostgreSQL localmente si utilizas la instancia de Neon.

## Cómo ejecutar

En Windows:

```powershell
.\gradlew.bat bootRun
```

En macOS/Linux:

```bash
./gradlew bootRun
```

La API queda disponible en `http://localhost:8080`.

## Probar con Postman

Para cada endpoint:

1. Crea una petición nueva y selecciona el método HTTP indicado.
2. Usa `http://localhost:8080` como base de la URL. Por ejemplo: `http://localhost:8080/api/requests`.
3. Para enviar un body, abre **Body → raw** y selecciona **JSON** en el selector de formato.
4. Postman debe enviar el encabezado `Content-Type: application/json` (normalmente lo agrega al seleccionar JSON).
5. Copia el ejemplo JSON indicado para ese endpoint. Las rutas `GET` y `DELETE`, así como `POST /api/requests/serve` y `POST /api/organizaciones/validar`, no requieren body.

No se requiere token para los endpoints documentados aquí. No se incluye una colección de Postman en este repositorio; puedes crear las peticiones manualmente siguiendo los ejemplos.

### Comprobar la API y la conexión a Neon

| Método | Ruta | Respuesta esperada |
|---|---|---|
| `GET` | `/api/health` | `200 OK` |
| `GET` | `/api/health/db` | `200` si la conexión a PostgreSQL en Neon funciona |

### Usuarios — `/api/usuarios`

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| `GET` | `/api/usuarios` | — | `200` con la lista |
| `GET` | `/api/usuarios/{id}` | — | `200` con el usuario o `404` si no existe |
| `POST` | `/api/usuarios` | JSON de creación | `201` con el usuario creado |
| `DELETE` | `/api/usuarios/{id}` | — | `200` con mensaje o `404` si no existe |

Ejemplo para `POST /api/usuarios`:

```json
{
  "nombreUsuario": "maria",
  "correo": "maria@example.com",
  "contrasenaHash": "hash-de-prueba",
  "telefono": "555-1234",
  "estado": "activo"
}
```

### Organizaciones — `/api/organizaciones`

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| `GET` | `/api/organizaciones` | — | `200` con las organizaciones activas |
| `GET` | `/api/organizaciones/{id}` | — | `200` con la organización activa o `404` |
| `POST` | `/api/organizaciones` | JSON de creación | `201`; la organización queda pendiente de validación |
| `GET` | `/api/organizaciones/pendientes` | — | `200` con las organizaciones pendientes |
| `POST` | `/api/organizaciones/validar` | — | `200` con la siguiente organización validada o `404` si no hay pendientes |

Ejemplo para `POST /api/organizaciones`:

```json
{
  "nombreOrg": "Comedor Comunitario",
  "tipoOrg": "Comedor comunitario",
  "responsable": "María López",
  "telefonoOrg": "555-1234",
  "correoOrg": "contacto@example.org",
  "calleOrg": "Reforma",
  "numeroOrg": "120",
  "idColonia": 1,
  "idUsuario": 2
}
```

### Solicitudes de apoyo — `/api/requests`

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| `GET` | `/api/requests` | — | `200` con las solicitudes pendientes |
| `GET` | `/api/requests/served` | — | `200` con el historial de solicitudes atendidas |
| `GET` | `/api/requests/{id}` | — | `200` con la solicitud o `404` si no existe |
| `POST` | `/api/requests` | JSON de creación | `201`; el motivo es obligatorio |
| `POST` | `/api/requests/serve` | — | `200` con la solicitud atendida o `404` si no hay pendientes |
| `PUT` | `/api/requests/{id}` | JSON con los datos actualizados | `200` con la solicitud o `404` |
| `DELETE` | `/api/requests/{id}` | — | `200` con mensaje o `404` si no existe |

Ejemplo para `POST /api/requests` o `PUT /api/requests/{id}`:

```json
{
  "fechaSolicitud": "2026-11-14",
  "motivo": "Apoyo con despensa para una familia",
  "numeroIntegrantes": 5,
  "prioridad": "alta"
}
```

La fecha usa el formato `AAAA-MM-DD`. En una actualización (`PUT`), `motivo` es obligatorio; también envía `numeroIntegrantes`, ya que el endpoint actualiza ese valor con el contenido del body. La fecha y prioridad pueden omitirse para conservar sus valores.

### Donaciones — `/api/donations`

| Método | Ruta | Body | Respuesta |
|---|---|---|---|
| `GET` | `/api/donations` | — | `200` con la lista |
| `GET` | `/api/donations/{id}` | — | `200` con la donación o `404` |
| `GET` | `/api/donations/organization/{idOrganizacion}` | — | `200` con donaciones de esa organización |
| `POST` | `/api/donations` | JSON de creación | `201`; cantidad e IDs deben ser mayores que cero |
| `PUT` | `/api/donations/{id}` | JSON con los datos actualizados | `200` con la donación, `400` por datos inválidos o `404` |
| `DELETE` | `/api/donations/{id}` | — | `200` con mensaje o `404` si no existe |

Ejemplo para `POST /api/donations` o `PUT /api/donations/{id}`:

```json
{
  "fechaDonacion": "2026-11-14",
  "cantidadTotal": 20,
  "estado": "pendiente",
  "observaciones": "Frutas y verduras en buen estado",
  "idComercio": 1,
  "idOrganizacion": 1
}
```

La fecha debe usar el formato `AAAA-MM-DD`; puede omitirse al crear. `cantidadTotal`, `idComercio` e `idOrganizacion` deben ser mayores que cero.

## Flujo de prueba sugerido

**Solicitudes (cola de atención)**

1. Ejecuta `GET /api/requests`: al iniciar, hay tres solicitudes de ejemplo pendientes.
2. Ejecuta `POST /api/requests/serve` sin body para atender la primera solicitud.
3. Vuelve a ejecutar `GET /api/requests`: la solicitud atendida ya no está en la lista de pendientes.
4. Ejecuta `GET /api/requests/served` para verla en el historial.
5. Repite la atención hasta vaciar la cola; la siguiente petición responde `404`.

**Organizaciones (cola de validación)**

1. Ejecuta `GET /api/organizaciones/pendientes` para ver las solicitudes de registro pendientes.
2. Ejecuta `POST /api/organizaciones/validar` sin body para validar la primera.
3. Ejecuta `GET /api/organizaciones` para consultar las organizaciones activas.

**Datos en memoria**

Los servicios cargan registros de ejemplo al iniciar. Los datos creados, modificados, atendidos o eliminados en una ejecución no se conservan al reiniciar la aplicación.

## Módulos aún no disponibles como endpoints

Existe un modelo y servicio para alimentos, pero `FoodController` todavía no implementa rutas REST. Por ello, las rutas `/api/foods` mencionadas en planes o pruebas anteriores aún no se pueden probar. Tampoco hay actualmente un controlador o endpoint REST para comercios.

## Estructuras de datos

Las estructuras se encuentran en `src/main/java/com/example/nutricom/structures/`. Los servicios exponen sus datos como listas de Java para responder en JSON.

| Estructura | Uso |
|---|---|
| `SinglyLinkedList` | Usuarios, organizaciones activas, alimentos, donaciones e historial de solicitudes |
| `Queue` | Solicitudes pendientes y organizaciones que esperan validación |

## Estructura del proyecto

```text
src/main/java/com/example/nutricom/
├── controllers/   # endpoints REST
├── services/      # lógica de negocio y datos en memoria
├── models/        # modelos del sistema
├── dto/           # datos recibidos en los bodies JSON
└── structures/    # Node, SinglyLinkedList y Queue
```

## Convenciones de trabajo

- Ramas: `main`, `develop` y ramas de trabajo `feature/...`, `fix/...` o `docs/...`.
- Commits con el formato `tipo: descripción`, por ejemplo: `feat: agrega módulo de solicitudes`.
- Cada cambio se integra mediante un Pull Request aprobado por otro integrante del equipo.
