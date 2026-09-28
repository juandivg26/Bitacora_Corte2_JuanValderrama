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
| `controller` | Endpoints REST |
| `exception` | Excepciones de negocio + `GlobalExceptionHandler` |
| `config` | Swagger y CORS |
| `util` | Utilidades compartidas (p. ej. `UuidV7Generator`) |

### 3.1 Estructura de paquetes

```text
src/main/java/com/restaurante/
├── RestauranteApplication.java
├── controller/        → endpoints REST (Plato, Menu, Mesa, Pedido, Cuenta, Reserva)
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
└── util/              → utilidades
```

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
| DELETE | `/{id}` | Eliminar un plato |

### 5.2 Menú (cliente) — `/api/v1/menu`
| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Ver el menú (solo platos disponibles) |
| GET | `/categoria/{categoria}` | Ver el menú por categoría (solo disponibles) |

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

---

## 6. Persistencia híbrida (PostgreSQL + MongoDB)

### 6.1 Configuración
`application.properties` contiene la configuración de conexión.

Ejemplo (ajusta según tu entorno):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/american_bites
spring.datasource.username=postgres
spring.datasource.password=TU_PASSWORD

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

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

## 10. Nota sobre el README (actualización importante)
Este README fue actualizado para reflejar que el proyecto **sí usa PostgreSQL** como persistencia real (ya no es solo en memoria).

---

## 11. Roles y permisos (RBAC) — S09

Se definieron los roles del restaurante y qué puede hacer cada uno. El detalle completo está en
[`docs/Roles_AmericanBites.xlsx`](docs/Roles_AmericanBites.xlsx) con 4 hojas:

| Hoja | Contenido |
|---|---|
| **Roles** | Descripción de cada rol y su alcance general. |
| **Matriz de permisos** | Las 34 funcionalidades (con su endpoint) frente a los 5 roles, con Sí/No. |
| **Detalle por Rol** | Para cada rol: qué PUEDE y qué NO puede hacer. |
| **Reglas de negocio por rol** | Reglas transversales (quién cobra, quién cambia estados, etc.). |

### 11.1 Roles del restaurante

| Rol | Descripción |
|---|---|
| **ADMINISTRADOR** | Gerente/dueño. Control total del sistema. |
| **MESERO** | Toma pedidos y gestiona mesas, cuentas y reservas. No administra la carta. |
| **COCINERO** | Consulta pedidos y avanza su estado (EN_PREPARACION / LISTO). |
| **CAJERO** | Registra el pago y cierra las cuentas de las mesas. |
| **CLIENTE** | Consulta el menú y gestiona sus propias reservas. |

### 11.2 Resumen de permisos clave

- **Crear / eliminar platos:** solo ADMINISTRADOR.
- **Ver el menú:** todos los roles (el CLIENTE solo ve platos disponibles).
- **Tomar pedidos (crear pedido / agregar ítems):** MESERO y ADMINISTRADOR.
- **Cambiar estado de un pedido:** COCINERO solo a `EN_PREPARACION` y `LISTO`; MESERO/ADMIN todas las transiciones válidas.
- **Registrar pago y cerrar cuenta:** CAJERO y ADMINISTRADOR. El MESERO abre la cuenta.
- **Reservas:** MESERO/ADMIN gestionan todas; el CLIENTE solo las suyas.

