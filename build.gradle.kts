import org.gradle.kotlin.dsl.implementation

plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.example"
//group = "com.aba.raffle"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")

dependencies {

    implementation("com.github.librepdf:openpdf:1.3.30")
    // =============================================
    // SPRING BOOT STARTERS (Funcionalidades base)
    // =============================================
    implementation("org.springframework.boot:spring-boot-starter-validation")  // Validación de datos
    implementation("org.springframework.boot:spring-boot-starter-web")         // Aplicación web REST
    implementation("org.springframework.boot:spring-boot-starter-mail")        // Envío de emails
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")    // Persistencia con JPA
    implementation("org.springframework.boot:spring-boot-starter-security")    // Seguridad y autenticación

    // =============================================
    // DOCUMENTACIÓN API
    // =============================================
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")  // Swagger/OpenAPI UI

    // =============================================
    // BASE DE DATOS
    // =============================================
    implementation("org.postgresql:postgresql")                                // Driver PostgreSQL
    runtimeOnly("org.postgresql:postgresql:42.7.3")                           // Runtime PostgreSQL

    // =============================================
    // SEGURIDAD & JWT
    // =============================================
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")                         // API JWT tokens
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")                           // Implementación JWT
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")                        // Serialización Jackson JWT

    // =============================================
    // MAPPER & CODE GENERATION
    // =============================================
    implementation("org.mapstruct:mapstruct:1.6.3")                           // Mapeo objeto-objeto
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")            // Procesador MapStruct
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")   // Integración Lombok-MapStruct

    // =============================================
    // LOMBOK (Reducción de código boilerplate)
    // =============================================
    compileOnly("org.projectlombok:lombok")                                   // Anotaciones Lombok
    annotationProcessor("org.projectlombok:lombok")                           // Procesador Lombok

    // =============================================
    // SERVICIOS EXTERNOS
    // =============================================
    implementation("com.cloudinary:cloudinary-http45:1.39.0")                 // Almacenamiento en cloud
    implementation("com.mercadopago:sdk-java:2.1.7") {                        // SDK MercadoPago
        exclude(group = "org.sonatype.sisu", module = "sisu-guice")           // Excluir dependencia conflictiva
    }
    implementation("com.google.inject:guice:5.1.0")                           // Inyección dependencias (para MercadoPago)

    // =============================================
    // TESTING
    // =============================================
    testImplementation("org.springframework.boot:spring-boot-starter-test")   // Testing Spring Boot
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc") // Documentación testing
    testImplementation("org.mockito:mockito-core:5.5.0")                      // Mocking para tests
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")             // Ejecutor pruebas JUnit

    // =============================================
    // DEPENDENCIAS ALTERNATIVAS (Comentadas)
    // =============================================
    /*
    // MercadoPago SDK (2.5.0) sin dependencia rota
    implementation("com.mercadopago:sdk-java:2.5.0") {
        exclude(group = "org.sonatype.sisu", module = "sisu-guice")
    }
    */
    //implementation("com.mercadopago:sdk-java:2.1.7")
    //implementation("com.google.inject:guice:4.2.3")
}

tasks.test {
    useJUnitPlatform()
    outputs.dir(project.extra["snippetsDir"]!!)
}


tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
