import org.gradle.kotlin.dsl.implementation

plugins {
    java
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.example"
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
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.0")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("com.github.librepdf:openpdf:1.3.30")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")

    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("com.cloudinary:cloudinary-http45:1.39.0")

    /*
    // MercadoPago SDK (2.5.0) sin dependencia rota
    implementation("com.mercadopago:sdk-java:2.5.0") {
        exclude(group = "org.sonatype.sisu", module = "sisu-guice")
    }
    */

    implementation("com.mercadopago:sdk-java:2.1.7") {
        exclude(group = "org.sonatype.sisu", module = "sisu-guice")
    }
    implementation("com.google.inject:guice:5.1.0")

    //implementation("com.mercadopago:sdk-java:2.1.7")

    //implementation("com.google.inject:guice:4.2.3")

    // PostgreSQL + JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
}

tasks.test {
    outputs.dir(project.extra["snippetsDir"]!!)
}

tasks.asciidoctor {
    inputs.dir(project.extra["snippetsDir"]!!)
    dependsOn(tasks.test)
}
