package com.challa.web.security

import org.springframework.beans.factory.InitializingBean
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets

@Component
class JwtSecretValidator(private val properties: JwtProperties, private val environment: Environment) :
    InitializingBean {
    override fun afterPropertiesSet() {
        validate(properties.secret, environment.activeProfiles.toList())
    }

    companion object {
        const val INSECURE_DEV_SECRET = "dev-only-insecure-secret-change-me-challa-0123456789"

        private val RELAXED_PROFILES = setOf("dev", "test", "local")
        private const val MIN_SECRET_BYTES = 32

        fun validate(secret: String, activeProfiles: List<String>) {
            if (activeProfiles.any { it.lowercase() in RELAXED_PROFILES }) return

            check(secret.isNotBlank()) {
                "jwt.secret is not configured. Set the JWT_SECRET environment variable, " +
                    "or activate the dev profile for local development."
            }
            check(secret != INSECURE_DEV_SECRET) {
                "jwt.secret is set to the well-known dev placeholder. Refusing to start outside " +
                    "the dev/test profiles - set a real JWT_SECRET."
            }
            check(secret.toByteArray(StandardCharsets.UTF_8).size >= MIN_SECRET_BYTES) {
                "jwt.secret must be at least $MIN_SECRET_BYTES bytes for HS256."
            }
        }
    }
}
