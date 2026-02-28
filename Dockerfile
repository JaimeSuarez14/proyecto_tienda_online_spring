# Dockerfile - Sprint 4
# Usar imagen base de Java 21 (versión Alpine para menor tamaño)
FROM eclipse-temurin:21-jdk-alpine AS build

# Instalar Maven para compilar (Alpine usa apk)
RUN apk add --no-cache maven

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos necesarios para compilar
# Primero copiamos solo los archivos de configuración para cachear dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Dar permisos de ejecución al wrapper
RUN chmod +x mvnw

# Descargar dependencias (esto se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el código fuente
COPY src src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Segunda etapa: imagen más pequeña solo con JRE
FROM eclipse-temurin:21-jre-alpine

# Crear usuario no root por seguridad
RUN addgroup -S spring && adduser -S spring -G spring

# Crear directorio para la aplicación
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Crear directorio para logs
RUN mkdir -p /app/logs && chown -R spring:spring /app

# Cambiar al usuario no root
USER spring:spring

# Exponer el puerto que usa la aplicación
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]