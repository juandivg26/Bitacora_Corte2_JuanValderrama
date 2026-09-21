# Sistema de Gestión de Restaurante - API REST

API REST desarrollada en **Java con Spring Boot** para la gestión integral de un restaurante, incluyendo mesas, pedidos, cuentas, platos y reservas.

Proyecto correspondiente a la **Bitácora del Corte 2**.

---

## Tecnologías Utilizadas

- **Java 21**&#x20;
- **Spring Boot**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
- **Maven** para la gestión de dependencias y construcción del proyecto
- **Springdoc OpenAPI / Swagger** para la documentación interactiva de la API
- **JUnit 5 & Mockito** para pruebas unitarias

### Capas principales

| Capa         | Responsabilidad                                     |
| ------------ | --------------------------------------------------- |
| `controller` | Recibe y responde peticiones HTTP                   |
| `service`    | Contiene la lógica de negocio                       |
| `repository` | Gestiona el acceso a la base de datos               |
| `model`      | Representa las entidades del dominio                |
| `dto`        | Define los datos de entrada y salida de la API      |
| `mapper`     | Convierte entidades a DTOs y viceversa              |
| `validator`  | Realiza validaciones específicas del negocio        |
| `exception`  | Gestiona y centraliza el manejo de errores          |
| `config`     | Contiene configuraciones generales de la aplicación |

---

## Funcionalidades Principales

### Gestión de Mesas

Permite administrar las mesas disponibles en el restaurante.

- Creación de mesas
- Actualización de mesas
- Consulta de mesas
- Consulta del estado de las mesas

### Gestión de Pedidos

Permite registrar y realizar seguimiento de los pedidos realizados por los clientes.

- Registro de pedidos
- Asociación de pedidos a una mesa
- Consulta de pedidos
- Seguimiento del estado de los pedidos

### Gestión de Cuentas

Permite gestionar las cuentas generadas por los clientes.

- Cálculo del valor de la cuenta
- Consulta de cuentas
- Cierre de cuentas

### Gestión de Platos

Permite administrar los platos disponibles en el menú.

- Creación de platos
- Actualización de platos
- Consulta de platos
- Administración del menú

### Gestión de Reservas

Permite controlar las reservas realizadas por los clientes.

- Creación de reservas
- Consulta de reservas
- Gestión de reservas por cliente
- Gestión de reservas por fecha

---

## Requisitos Previos

Antes de ejecutar el proyecto, es necesario contar con:

- **JDK 21**
- **Apache Maven 3.8+**

Para verificar las versiones instaladas:

```bash
java -version
mvn -version
```

---

## Instalación y Ejecución

### 1. Clonar el repositorio

```bash
git clone <URL_DEL_REPOSITORIO>
```

Ingresar al directorio del proyecto:

```bash
cd Bitacora-2/Bitacora_Corte2_JuanValderrama
```

---

### 2. Compilar el proyecto

Ejecutar:

```bash
mvn clean package
```

Este comando limpia compilaciones anteriores, compila el proyecto y genera el archivo ejecutable correspondiente.

---

### 3. Ejecutar la aplicación

Para iniciar la aplicación con Spring Boot:

```bash
mvn spring-boot:run
```

La aplicación estará disponible por defecto en:

```text
http://localhost:8080
```

---

## Documentación de la API

El proyecto utiliza **Springdoc OpenAPI / Swagger** para generar documentación interactiva de los endpoints disponibles.

Con la aplicación ejecutándose, se puede acceder a:

### Swagger UI

```text
http://localhost:8080/swagger-ui.html
```

Desde Swagger UI es posible:

- Consultar los endpoints disponibles.
- Revisar los métodos HTTP.
- Consultar parámetros.
- Consultar estructuras de Request y Response.
- Ejecutar peticiones directamente contra la API.

### OpenAPI Docs

```text
http://localhost:8080/v3/api-docs
```

Este endpoint proporciona la especificación OpenAPI de la API.

---

## Pruebas Unitarias

El proyecto utiliza **JUnit 5 y Mockito** para realizar pruebas unitarias.

Para ejecutar todas las pruebas:

```bash
mvn test
```

Las pruebas permiten verificar el comportamiento de los diferentes componentes de la aplicación y detectar errores antes de realizar cambios o nuevas implementaciones.

---

## Construcción del Proyecto

Para generar el archivo `.jar` ejecutable:

```bash
mvn clean package
```

El archivo generado se encontrará normalmente en:

```text
target/
```

Para ejecutar el `.jar`:

```bash
java -jar target/<nombre-del-archivo>.jar
```

---

## URL Base

Una vez iniciada la aplicación:

```text
http://localhost:8080
```

---

## Proyecto Académico

**Proyecto:** Sistema de Gestión de Restaurante - API REST
**Asignatura:** Desarrollo de Software
**Tecnología principal:** Java + Spring Boot
