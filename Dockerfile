# Usar imagen base de Java
FROM eclipse-temurin:21-jdk-jammy

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el jar compilado
COPY build/libs/*.jar app.jar

# Exponer el puerto (cambia 8080 si tu app usa otro)
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]