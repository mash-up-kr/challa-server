package com.challa.bootstrap.secret

import org.springframework.boot.EnvironmentPostProcessor
import org.springframework.boot.SpringApplication
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

class SecretEnvironmentPostProcessor : EnvironmentPostProcessor {
    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        if (environment.activeProfiles.any { it == "test" }) return

        val clientId = environment.getRequiredProperty("infisical.client-id")
        val clientSecret = environment.getRequiredProperty("infisical.client-secret")
        val projectId = environment.getRequiredProperty("infisical.project-id")

        val activeProfile = environment.activeProfiles.firstOrNull() ?: "dev"

        val client = InfisicalClient(
            clientId = clientId,
            clientSecret = clientSecret
        )

        val secrets = client.loadSecrets(
            projectId = projectId,
            environment = activeProfile
        )

        environment.propertySources.addFirst(
            MapPropertySource(
                "infisical-secrets",
                secrets
            )
        )
    }
}
