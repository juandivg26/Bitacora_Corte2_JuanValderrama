# American Bites — API REST del Restaurante

**Autor:** Juan Diego Valderrama Gaviria
**Asignatura:** Diseño y Construcción de Software (DOSW) — Escuela Colombiana de Ingeniería Julio Garavito
**Entrega:** Bitácora - Restaurante API (Manejo Básico S7 y S8), Corte 2

---

## Descripción

API REST para la gestión operativa de **American Bites**, un restaurante de **comida rápida**. El sistema administra la carta de platos, las mesas del local, los pedidos que se toman en cada mesa, el cobro de cuentas y las reservas.

El proyecto está construido en **Java 21 + Spring Boot**, siguiendo una arquitectura por capas (Dominio → DTO → Mapper → Service/Validator → Controller → Exception Handler). **No usa base de datos**: toda la información se guarda en memoria (`ConcurrentHashMap`) mientras la aplicación está corriendo, por lo que los datos se reinician cada vez que se reinicia el servidor.

### Tecnologías

| Tecnología | Uso |
|---|---|
| Java 21 | Lenguaje base |
| Spring Boot 3.3.4 (Web + Validation) | Framework REST |
| Maven | Gestión de dependencias y build |
| Lombok | Reducción de boilerplate (`@Data`, `@Slf4j`, `@RequiredArgsConstructor`) |
| MapStruct | Mapeo automático entre DTOs y dominio |
| springdoc-openapi (Swagger UI) | Documentación interactiva de la API |
| JUnit 5 + Mockito | Pruebas unitarias |
| JaCoCo | Cobertura de pruebas |
| SonarQube / SonarCloud | Análisis estático de código |

### Arquitectura por capas

| Capa | Responsabilidad |
|---|---|
| `model/domain` | Entidades del dominio y su lógica de negocio propia (`Plato`, `Mesa`, `Pedido`, `ItemPedido`, `Cuenta`, `Reserva`) |
| `model/dto/request` `model/dto/response` | Contratos de entrada/salida de la API, con validaciones `@Valid` |
| `mapper` | Traduce entre DTOs y dominio (MapStruct) |
| `service` / `service/impl` | Lógica de negocio y orquestación |
| `validator` / `validator/impl` | Validaciones de negocio (duplicados, transiciones de estado, reglas propias) |
| `controller` | Expone los endpoints REST |
| `exception` | Excepciones de dominio + `GlobalExceptionHandler` centralizado |
| `config` | Swagger y CORS |

---

## Funcionalidades

### Gestión de Platos (carta)
Crear, actualizar, consultar y activar/desactivar disponibilidad de los platos del menú, con filtro por categoría.

### Gestión de Mesas
Registrar mesas, consultar su estado (`DISPONIBLE` / `OCUPADA` / `RESERVADA`) y cambiarlo.

### Menú (vista del cliente)
Vista de solo lectura de la carta, mostrando únicamente los platos disponibles — pensada para el cliente, sin las operaciones administrativas de `Platos`.

### Gestión de Pedidos
Tomar un pedido para una mesa (con uno o más ítems), agregar ítems mientras el pedido siga en estado `RECIBIDO`, y avanzarlo por sus estados (`RECIBIDO → EN_PREPARACION → LISTO → ENTREGADO`, o `CANCELADO` desde los dos primeros). El precio de cada ítem se **congela** en el momento del pedido, aunque el plato cambie de precio después.

### Gestión de Cuentas
Abrir la cuenta de una mesa, registrar el pago (calculando el total a partir de los pedidos de esa mesa) y cerrarla, liberando la mesa.

### Gestión de Reservas
Reservar una mesa disponible para una fecha futura, cancelar una reserva vigente o reprogramarla.

### Reglas de negocio propias del concepto
- Un plato no disponible no puede pedirse ni reservarse mesa alguna con él.
- Una mesa solo puede tener **una** cuenta abierta a la vez.
- Una mesa solo puede reservarse si está `DISPONIBLE`.
- Las transiciones de estado (de `Pedido`, `Mesa`, `Cuenta`) están controladas: no se puede saltar pasos ni retroceder arbitrariamente.
- El precio de cada ítem de un pedido queda congelado al momento de pedirlo.

---

## Endpoints

### Platos — `/api/v1/platos`

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

### Menú (cliente) — `/api/v1/menu`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Ver el menú (solo platos disponibles) |
| GET | `/categoria/{categoria}` | Ver el menú por categoría (solo disponibles) |

### Mesas — `/api/v1/mesas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las mesas |
| GET | `/disponibles` | Listar mesas disponibles |
| GET | `/{id}` | Obtener una mesa por ID |
| POST | `/` | Crear una mesa |
| PATCH | `/{id}/estado?nuevoEstado=` | Cambiar el estado de una mesa |
| DELETE | `/{id}` | Eliminar una mesa |

### Pedidos — `/api/v1/pedidos`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todos los pedidos |
| GET | `/mesa/{idMesa}` | Listar pedidos de una mesa |
| GET | `/{id}` | Obtener un pedido por ID |
| POST | `/` | Crear un pedido (con sus ítems) |
| POST | `/{id}/items` | Agregar un ítem a un pedido existente |
| PATCH | `/{id}/estado` | Cambiar el estado de un pedido |

### Cuentas — `/api/v1/cuentas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las cuentas |
| GET | `/{id}` | Obtener una cuenta por ID |
| GET | `/mesa/{idMesa}` | Obtener la cuenta abierta de una mesa |
| POST | `/` | Abrir la cuenta de una mesa |
| PATCH | `/{id}/pago` | Registrar el pago de una cuenta |
| PATCH | `/{id}/cerrar` | Cerrar una cuenta |

### Reservas — `/api/v1/reservas`

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/` | Listar todas las reservas |
| GET | `/mesa/{idMesa}` | Listar reservas de una mesa |
| GET | `/{id}` | Obtener una reserva por ID |
| POST | `/` | Crear una reserva |
| PATCH | `/{id}/cancelar` | Cancelar una reserva |
| PATCH | `/{id}/reprogramar` | Reprogramar una reserva |

---

## Cómo ejecutar el proyecto

```bash
git clone https://github.com/juandivg26/Bitacora_Corte2_JuanValderrama.git
cd Bitacora_Corte2_JuanValderrama
mvn clean install
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

Para correr las pruebas y generar el reporte de cobertura:

```bash
mvn clean test
```

El reporte de JaCoCo queda en `target/site/jacoco/index.html`.

---

