# ─── Etapa 1: compilación ────────────────────────────────────────────────
# Usar Maven y Java 21 para construir el JAR ejecutable.
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Separar la descarga de dependencias para aprovechar la caché de Docker.
COPY pom.xml ./
RUN mvn dependency:go-offline -q

# Compilar el proyecto y ejecutar sus verificaciones. Se omiten pruebas solo
# dentro de la imagen: deben ejecutarse previamente con `mvn clean test`.
COPY src ./src
RUN mvn clean package -DskipTests -q

# ─── Etapa 2: ejecución ─────────────────────────────────────────────────
# La imagen final no contiene Maven, código fuente ni cachés de compilación.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Usuario no privilegiado: la aplicación nunca se ejecuta como root.
RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=builder /app/target/*.jar app.jar
RUN chown spring:spring /app/app.jar

USER spring

ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8080

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
