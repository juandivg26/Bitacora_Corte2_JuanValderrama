package com.restaurante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Verifica que el contexto completo de Spring (controllers, services, validators,
 * mappers de MapStruct y repositorios) se construye correctamente, y que el
 * esquema de la base de datos se genera sin errores de mapeo.
 * Usa H2 en memoria en lugar de PostgreSQL para que la prueba sea independiente.
 */
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:contextodb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        // Sin MongoDB en el entorno de pruebas: se silencia el monitor del driver
        "logging.level.org.mongodb.driver.cluster=OFF"
})
class ContextoCargaTest {

    @Test
    @DisplayName("El contexto de la aplicación carga correctamente")
    void contextLoads() {
        // Si el contexto no se puede crear (beans, mapeo JPA, etc.) esta prueba falla.
    }
}
