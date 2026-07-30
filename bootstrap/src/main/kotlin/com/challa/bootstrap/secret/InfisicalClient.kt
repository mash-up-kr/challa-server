package com.challa.bootstrap.secret

import com.infisical.sdk.InfisicalSdk
import com.infisical.sdk.config.SdkConfig

class InfisicalClient(private val clientId: String, private val clientSecret: String) {
    private val sdk = InfisicalSdk(
        SdkConfig.Builder().build()
    )

    init {
        sdk.Auth().UniversalAuthLogin(clientId, clientSecret)
    }

    fun loadSecrets(projectId: String, environment: String, secretPath: String = "/"): Map<String, String> {
        val secrets = sdk.Secrets().ListSecrets(
            projectId,
            environment,
            secretPath,
            true,
            true,
            false,
            false
        )

        return secrets.associate { it.secretKey to it.secretValue }
    }
}
