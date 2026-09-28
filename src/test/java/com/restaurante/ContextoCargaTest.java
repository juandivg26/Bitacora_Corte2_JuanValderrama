package com.restaurante;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

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

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    @DisplayName("El contexto de la aplicación carga correctamente")
    void contextLoads() {
        // Si el contexto no se puede crear (beans, mapeo JPA, etc.) esta prueba falla.
    }

    @Test
    @DisplayName("Existe un único PlatformTransactionManager para que @Transactional funcione")
    void existeUnUnicoTransactionManager() {
        assertNotNull(transactionManager);
        assertEquals(1, applicationContext.getBeansOfType(PlatformTransactionManager.class).size(),
                "Con dos gestores de transacciones (JPA + Mongo) @Transactional fallaría en runtime");
    }
}
