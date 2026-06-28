plugins {
    kotlin("plugin.spring")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":challa-core"))

    implementation("org.springframework.boot:spring-boot-starter-web")
}