plugins {
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":challa-core"))
    implementation(project(":challa-web"))
    implementation(project(":challa-external-in"))
    implementation(project(":challa-external-out"))
    implementation(project(":challa-persistence"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("com.infisical:sdk:3.0.7") // Infisical
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
