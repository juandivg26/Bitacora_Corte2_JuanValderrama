# American Bites — API REST del Restaurante

**Autor:** Juan Diego Valderrama Gaviria  
**Asignatura:** Diseño y Construcción de Software (DOSW) — Escuela Colombiana de Ingeniería Julio Garavito  
**Entrega:** Bitácora - Restaurante API (Semanas S7, S8 y S9), Corte 2

---

## 1. Descripción
API REST para la gestión operativa de **American Bites**, un restaurante de **comida rápida**. El sistema administra la carta de platos, las mesas del local, los pedidos que se toman en cada mesa, el cobro de cuentas y las reservas.

> **Persistencia real:** A partir de la versión de esta entrega, la aplicación **usa PostgreSQL** (Spring Data JPA). Los datos se guardan en la base de datos y no se reinician al reiniciar la app.

El proyecto está construido en **Java 21 + Spring Boot**, siguiendo una arquitectura por capas (Dominio → DTO → Mapper → Service/Validator → Controller → Exception Handler).

---

## 2. Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje base |
| Spring Boot 3.3.4 (Web + Validation) | Framework REST |
| Spring Data JPA + Hibernate | Persistencia: entidades y repositorios |
| PostgreSQL | Base de datos relacional |
| Maven | Gestión de dependencias y build |
| Lombok | Reducción de boilerplate (`@Data`, `@Slf4j`, `@RequiredArgsConstructor`) |
| MapStruct | Mapeo automático (DTO ↔ dominio y dominio ↔ entidad JPA) |
| springdoc-openapi (Swagger UI) | Documentación interactiva de la API |
| JUnit 5 + Mockito | Pruebas unitarias |
| H2 (solo en pruebas) | Base de datos embebida para las pruebas de persistencia |
| MongoDB (Spring Data MongoDB) | Persistencia NoSQL: log de eventos de pedidos |
| JaCoCo | Cobertura de pruebas |

---

## 3. Arquitectura por capas

| Capa | Responsabilidad |
|---|---|
| `model/domain` | Objetos de dominio y su lógica de negocio (`Plato`, `Mesa`, `Pedido`, `ItemPedido`, `Cuenta`, `Reserva`) + enums de estado |
| `model/entity` | Entidades JPA que representan las tablas (`@Entity`, `@ManyToOne`, `@OneToMany`). Sin lógica de negocio |
| `model/document` | Documentos NoSQL (`@Document`) almacenados en MongoDB. Sin lógica de negocio |
| `model/dto/request` / `model/dto/response` | Contratos de entrada/salida de la API (validaciones con `@Valid`) |
| `mapper` | Traduce entre DTO ↔ dominio y dominio ↔ entidades JPA (MapStruct) |
| `repository` | Interfaces Spring Data JPA (un repositorio por entidad) |
| `service` / `service/impl` | Lógica de negocio y orquestación (invoca repositorios + validadores) |
| `validator` / `validator/impl` | Reglas de negocio y validaciones (duplicados, transiciones de estado, etc.) |
| `controller` | Endpoints REST (solo las anotaciones de Spring MVC) |
| `controller/docs` | Contratos documentados de la API: interfaces con las anotaciones de Swagger (`@Tag`, `@Operation`, `@ApiResponse`). Los controllers las implementan y quedan limpios |
| `exception` | Excepciones de negocio + `GlobalExceptionHandler` |
| `config` | Swagger y CORS |
| `util` | Utilidades compartidas (`UuidV7Generator`, `FechaUtils`, `CalculoUtils`, `TextoUtils`) |

### 3.1 Estructura de paquetes

```text
src/main/java/com/restaurante/
├── RestauranteApplication.java
├── controller/        → endpoints REST (Plato, Menu, Mesa, Pedido, Cuenta, Reserva, EventoPedido)
│   └── docs/          → interfaces con la documentacion de Swagger
├── service/           → interfaces de servicio
│   └── impl/          → implementaciones (@Service)
├── repository/        → interfaces Spring Data JPA (una por entidad)
├── model/
│   ├── domain/        → dominio + enums de estado
│   ├── entity/        → entidades JPA
│   └── dto/
│       ├── request/   → DTOs de entrada (@Valid)
│       └── response/  → DTOs de salida
├── mapper/            → MapStruct (DTO ↔ dominio y dominio ↔ entidad)
├── validator/         → reglas de negocio
│   └── impl/
├── exception/         → excepciones + GlobalExceptionHandler
├── config/            → Swagger y CORS
└── util/              → utilidades (UuidV7Generator, FechaUtils, CalculoUtils, TextoUtils)
```

### 3.2 Utilidades compartidas (`util/`)

| Utilidad | Métodos | Usada en |
|---|---|---|
| `FechaUtils` | `esFechaFutura`, `formatearFecha`, `formatearIso`, `seSolapan` | `ReservaValidatorImpl` (fecha futura y solapamiento de reservas) |
| `CalculoUtils` | `subtotal`, `sumar`, `redondear`, `aplicarDescuento` | `ItemPedido.subtotal()`, `Pedido.total()`, `Cuenta.calcularTotal()` |
| `TextoUtils` | `normalizar`, `sonIgualesNormalizados`, `normalizarNombre`, `esVacio` | `PlatoServiceImpl` (filtro por categoría y nombre), `PlatoValidatorImpl` |
| `UuidV7Generator` | `generate()` | `PedidoServiceImpl`, `ReservaServiceImpl` |

---

## 4. Reglas de negocio principales
- Un plato **no disponible** no puede pedirse ni agregarse como ítem de un pedido.
- Una mesa solo puede tener **una cuenta abierta** a la vez.
- Una mesa solo puede reservarse si está `DISPONIBLE`.
- Un pedido solo puede modificarse (agregar ítems) mientras esté en estado `RECIBIDO`.
- Las transiciones de estado de `Pedido`, `Mesa` y `Cuenta` están controladas; una transición inválida responde **422**.
- El precio de cada ítem queda **congelado** (`precioCongelado`) en el momento de agregarlo al pedido.
- El total de la cuenta se calcula sumando los ítems de los pedidos **no cancelados** de la mesa.
- Un plato con nombre duplicado y una mesa con número duplicado se rechazan con **409**.
- Al cerrar la cuenta, la mesa queda `DISPONIBLE` y con `cuentaAbierta = false`.
- Una reserva cancelada no puede modificarse ni reprogramarse.
- Una mesa no puede tener dos reservas vigentes que se solapen (cada reserva ocupa un bloque de 2 horas).
- Un plato con **pedidos activos** (pedidos que no están `ENTREGADOS` ni `CANCELADOS`) **no se puede eliminar**: responde **409**.
- Un pedido se puede cancelar en `RECIBIDO` y en `EN_PREPARACION`, pero no cuando ya está `LISTO` o `ENTREGADO` (decisión documentada en `EstadoPedido`).

---

## 5. Endpoints

### 5.1 Platos — `/api/v1/platos`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todos los platos |
| GET | `/disponibles` | Listar solo los disponibles |
| GET | `/categoria/{categoria}` | Filtrar por categoría |
| GET | `/{id}` | Obtener un plato por ID |
| POST | `/` | Crear un plato |
| PUT | `/{id}` | Actualizar un plato |
| PATCH | `/{id}/disponible?disponible=` | Cambiar disponibilidad |
| DELETE | `/{id}` | Eliminar un plato (409 si tiene pedidos activos) |

### 5.2 Menú (cliente) — `/api/v1/menu`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Ver el menú (solo platos disponibles) |
| GET | `/{id}` | Ver el detalle de un plato del menú (404 si el plato no existe o está desactivado) |
| GET | `/categoria/{categoria}` | Ver el menú por categoría (solo disponibles; ignora mayúsculas y acentos) |

### 5.3 Mesas — `/api/v1/mesas`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las mesas |
| GET | `/disponibles` | Listar mesas disponibles |
| GET | `/{id}` | Obtener una mesa por ID |
| POST | `/` | Crear una mesa |
| PATCH | `/{id}/estado?nuevoEstado=` | Cambiar el estado de una mesa |
| DELETE | `/{id}` | Eliminar una mesa |

### 5.4 Pedidos — `/api/v1/pedidos`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todos los pedidos |
| GET | `/mesa/{idMesa}` | Listar pedidos de una mesa |
| GET | `/{id}` | Obtener un pedido por ID |
| POST | `/` | Crear un pedido (con sus ítems) |
| POST | `/{id}/items` | Agregar un ítem a un pedido existente |
| PATCH | `/{id}/estado` | Cambiar el estado de un pedido |

### 5.5 Cuentas — `/api/v1/cuentas`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las cuentas |
| GET | `/{id}` | Obtener una cuenta por ID |
| GET | `/mesa/{idMesa}` | Obtener la cuenta abierta de una mesa |
| POST | `/` | Abrir la cuenta de una mesa |
| PATCH | `/{id}/pago` | Registrar el pago de una cuenta |
| PATCH | `/{id}/cerrar` | Cerrar una cuenta |

### 5.6 Reservas — `/api/v1/reservas`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las reservas |
| GET | `/mesa/{idMesa}` | Listar reservas de una mesa |
| GET | `/{id}` | Obtener una reserva por ID |
| POST | `/` | Crear una reserva |
| PATCH | `/{id}/cancelar` | Cancelar una reserva |
| PATCH | `/{id}/reprogramar` | Reprogramar una reserva |

### 5.7 Eventos de pedido (MongoDB) — `/api/v1/eventos`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todos los eventos (del más reciente al más antiguo) |
| GET | `/pedido/{idPedido}` | Listar los eventos de un pedido |

### 5.8 Códigos de error

Todas las respuestas de error usan el mismo cuerpo (`ErrorResponseDTO`):

```json
{
  "timestamp": "2026-09-28T02:51:31.69",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "No se puede pasar el pedido de RECIBIDO a ENTREGADO",
  "path": "/api/v1/pedidos/01a0e6ff-367e-756a-a2a1-2329435c8866/estado"
}
```

| Código | Cuándo se devuelve | Ejemplo |
|---|---|---|
| **400** Bad Request | El request no cumple el contrato: body vacío, campo inválido, JSON mal formado, tipo de dato incorrecto, parámetro obligatorio faltante | `POST /platos` con `{}` |
| **404** Not Found | El recurso no existe (o la ruta no está mapeada) | `GET /platos/999` |
| **405** Method Not Allowed | El verbo HTTP no está soportado en esa ruta | `PUT /mesas/1` |
| **409** Conflict | Conflicto con el estado actual del recurso | nombre de plato duplicado; eliminar un plato con pedidos activos |
| **422** Unprocessable Entity | El request está bien formado pero viola una regla de negocio | transición de estado inválida |
| **500** Internal Server Error | Falla inesperada del servidor | — |

> `GlobalExceptionHandler` hereda de `ResponseEntityExceptionHandler`, por lo que los errores propios de
> Spring MVC (JSON ilegible, tipo de parámetro inválido, parámetro faltante, verbo no soportado) se
> traducen a 400/405 en vez de caer en el 500 genérico.

---

## 6. Persistencia híbrida (PostgreSQL + MongoDB)

### 6.1 Configuración
`application.properties` contiene la configuración de conexión. **Las credenciales no se versionan**:
se leen de variables de entorno con valores por defecto, y el proyecto carga además un archivo local
`.env.properties` (ignorado por git) para el desarrollo.

```properties
# application.properties (versionado, sin credenciales)
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/american_bites}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:}
spring.jpa.hibernate.ddl-auto=${JPA_DDL_AUTO:update}

# .env.properties (NO versionado)
DB_PASSWORD=tu_password
```

También puedes exportar las variables `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `MONGODB_URI`,
`JPA_DDL_AUTO` y `JPA_SHOW_SQL` directamente en el entorno.

### 6.2 Ejecución de migración de esquema
Se usa `spring.jpa.hibernate.ddl-auto=update`, por lo que Hibernate crea/actualiza las tablas automáticamente al iniciar la app.

### 6.3 Modelo de datos (entidades y relaciones)

| Entidad | Tabla | Relaciones |
|---|---|---|
| `PlatoEntity` | `platos` | — |
| `MesaEntity` | `mesas` | — |
| `PedidoEntity` | `pedidos` | `@OneToMany` → `ItemPedidoEntity`, `@ManyToOne` → `MesaEntity` |
| `ItemPedidoEntity` | `items_pedido` | `@ManyToOne` → `PedidoEntity` |
| `CuentaEntity` | `cuentas` | `@ManyToOne` → `MesaEntity` |
| `ReservaEntity` | `reservas` | `@ManyToOne` → `MesaEntity` |

Todas las asociaciones usan `FetchType.LAZY`. La relación con `MesaEntity` es unidireccional: la FK (`id_mesa`) se escribe desde la asociación y se expone además como columna de solo lectura, para poder filtrar por `idMesa` sin inicializar la relación.

### 6.4 Estrategia de identificadores

| Entidad | Tipo de ID | Motivo |
|---|---|---|
| `Plato`, `Mesa`, `ItemPedido`, `Cuenta` | `Long` auto-incremental | Entidades internas con muchas relaciones; el ID lo genera la base de datos. |
| `Pedido`, `Reserva` | `UUID v7` | Identificadores expuestos en las URLs; no revelan la secuencia y están ordenados por tiempo (no fragmentan el índice). |

### 6.5 Módulo NoSQL (MongoDB): log de eventos de pedidos

El proyecto usa **persistencia híbrida**. El núcleo del restaurante (platos, mesas, pedidos, cuentas, reservas) vive en **PostgreSQL** porque necesita transacciones y relaciones fuertes (ACID). En cambio, el **historial de cambios de estado de los pedidos** vive en **MongoDB**, porque:

- es un log **append-only** (solo se inserta y se consulta, nunca se actualiza),
- tiene **alto volumen de escritura** y no necesita `JOIN`s,
- la información se lee siempre completa y junta → los ítems se guardan **embebidos**.

| Elemento | Ubicación |
|---|---|
| Dominio | `model/domain/EventoPedido.java` |
| Documento | `model/document/EventoPedidoDocument.java` (+ `ItemEventoDocument`) |
| Repositorio | `repository/IEventoPedidoRepository.java` (`MongoRepository`) |
| Mapper | `mapper/EventoPedidoDocumentMapper.java` (dominio ↔ documento) |
| Servicio | `service/IEventoPedidoService.java` + `service/impl/EventoPedidoServiceImpl.java` |
| Endpoints | `GET /api/v1/eventos` y `GET /api/v1/eventos/pedido/{idPedido}` |

Los eventos se registran automáticamente al **crear un pedido** y al **cambiar su estado**. El registro es **no crítico**: si MongoDB no está disponible, se registra un `WARN` y el flujo del pedido continúa con normalidad.

### 6.6 Diagramas

Fuente editable (`.puml`) y render (`.png`) en [`docs/diagramas/`](docs/diagramas):

| Archivo | Contenido |
|---|---|
| `01-contexto` | Diagrama de contexto (actores y sistema) |
| `02-modelo-entidad-relacion` | Modelo Entidad-Relación de PostgreSQL |
| `03-normalizacion` | Justificación de 1FN, 2FN y 3FN |
| `04-modelo-documentos` | Modelo de documentos de MongoDB (embebido vs referenciado) |
| `05-modelo-clases` | Modelo de clases del dominio y enums de estado |

### 6.7 Transacciones: ACID en PostgreSQL, BASE en MongoDB

Los casos de uso que escriben en **más de una tabla** están anotados con `@Transactional`, para que el
conjunto se confirme o se revierta como una sola unidad (atomicidad):

| Flujo | Escrituras que deben ser atómicas |
|---|---|
| `PedidoServiceImpl.crear` | inserta el pedido con sus ítems **y** deja la mesa `OCUPADA` |
| `CuentaServiceImpl.abrir` | inserta la cuenta **y** marca la mesa con `cuentaAbierta = true` |
| `CuentaServiceImpl.cerrar` | cierra la cuenta **y** libera la mesa |
| `ReservaServiceImpl.crear` / `cancelar` | guarda la reserva **y** cambia el estado de la mesa |
| `PlatoServiceImpl.eliminar` | valida que no haya pedidos activos **y** borra el plato |

El registro de eventos en MongoDB es **best effort**: se ejecuta dentro del flujo pero envuelto en
`try/catch` (es un dato no crítico) y **no forma parte de la transacción relacional**. MongoDB no
garantiza ACID como PostgreSQL, y es justo el caso que cubre el modelo **B.A.S.E.**: el log es
eventualmente consistente y su fallo no debe tumbar la operación del restaurante.

> La prueba `ContextoCargaTest.existeUnUnicoTransactionManager` garantiza que exista **un solo**
> `PlatformTransactionManager`: con JPA y MongoDB conviviendo podrían registrarse dos y `@Transactional`
> fallaría en runtime.

---

## 7. Cómo ejecutar el proyecto

### 7.1 Requisitos
- PostgreSQL en ejecución y base de datos `american_bites` creada
- MongoDB en ejecución (local o MongoDB Atlas). Configurar la URI en `application.properties` (`spring.data.mongodb.uri`)

### 7.2 Comandos
```bash
git clone https://github.com/juandivg26/Bitacora_Corte2_JuanValderrama.git
cd Bitacora_Corte2_JuanValderrama

mvn clean install
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

---

## 8. Pruebas locales

```bash
mvn clean test
```

La batería incluye:
- **Pruebas unitarias**: servicios (con Mockito), validadores, mappers y utilidades.
- **`PersistenciaRelacionesJpaTest`**: prueba de persistencia real sobre H2 que verifica el mapeo de las entidades y sus relaciones.
- **`ContextoCargaTest`**: verifica que el contexto completo de Spring (beans, mappers y mapeo JPA) arranca correctamente.

- Reporte de JaCoCo: `target/site/jacoco/index.html`

### 8.1 Análisis estático con SonarQube

El proyecto ya trae el plugin `sonar-maven-plugin` y el archivo [`sonar-project.properties`](sonar-project.properties)
configurado (fuentes, pruebas, binarios, clases de test y las rutas de cobertura de JaCoCo).

```bash
# 1) compila y ejecuta las pruebas (genera target/site/jacoco/jacoco.xml)
mvn clean test

# 2) lanza el analisis
#    SonarCloud
mvn sonar:sonar -Dsonar.host.url=https://sonarcloud.io -Dsonar.organization=TU_ORG -Dsonar.token=TU_TOKEN
#    SonarQube local (por ejemplo con Docker en http://localhost:9000)
mvn sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.token=TU_TOKEN
```

> El token se genera en *My Account → Security* de SonarQube/SonarCloud. Guarda el dashboard o
> el reporte de `target/sonar/` como evidencia de la entrega.

---

## 9. Ejemplos de demostración (verificación en Swagger + DBeaver)

A continuación se muestran ejemplos concretos para demostrar el flujo **con persistencia real**.

### 9.1 Crear una mesa
**Request (Swagger):** `POST /api/v1/mesas`
```json
{
  "numero": 10,
  "capacidad": 4
}
```

**Verificación (DBeaver):**
```sql
SELECT * FROM mesas;
```

### 9.2 Crear un pedido con ítems (congelación de precio)
1) Crear/asegurar un plato disponible (ej. Hamburguesa id=1)
2) Usar la `idMesa` creada

**Request (Swagger):** `POST /api/v1/pedidos`
```json
{
  "idMesa": 4,
  "items": [
    {
      "idPlato": 1,
      "cantidad": 2
    }
  ]
}
```

**Verificación (DBeaver):**
```sql
SELECT * FROM pedidos;       -- incluye la FK id_mesa
SELECT * FROM items_pedido;  -- incluye el precioCongelado
```

> Se evidencia que el `precioCongelado` se guarda en la tabla de ítems de pedido.

### 9.3 Abrir cuenta y cerrar (total calculado)
**Request (Swagger):** `POST /api/v1/cuentas`
```json
{
  "idMesa": 4
}
```

**Request (Swagger):** `PATCH /api/v1/cuentas/{id}/pago`
```http
PATCH /api/v1/cuentas/3/pago
```

**Request (Swagger):** `PATCH /api/v1/cuentas/{id}/cerrar`
```http
PATCH /api/v1/cuentas/3/cerrar
```

**Verificación (DBeaver):**
```sql
SELECT * FROM cuentas;
SELECT * FROM mesas WHERE id = 4;
```

> En la demostración se observó que el campo `total` queda con el valor calculado (ej. 50000.0) y el estado queda `CERRADA`.

### 9.4 Reservas
**Request (Swagger):** `POST /api/v1/reservas`
```json
{
  "idMesa": 4,
  "cliente": "Juan Pérez",
  "fechaHora": "2026-09-28T18:00:00",
  "comensales": 4
}
```

**Verificación (DBeaver):**
```sql
SELECT * FROM reservas;
```

---


