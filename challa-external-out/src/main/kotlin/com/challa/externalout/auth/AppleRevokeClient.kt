package com.challa.externalout.auth

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class AppleRevokeClient(properties: SocialRevokeProperties) {
    private val config = properties.apple
    private val clientSecretGenerator = AppleClientSecretGenerator(config)
    private val restClient = RestClient.create()

    fun revoke(authorizationCode: String?) {
        check(!authorizationCode.isNullOrBlank()) {
            "No Apple authorization code stored; cannot revoke the account link"
        }
        val clientSecret = clientSecretGenerator.generate()
        val tokens = exchangeAuthorizationCode(authorizationCode, clientSecret)
        val token = tokens.refreshToken ?: tokens.accessToken
        check(token != null) { "Apple token exchange returned no token to revoke" }

        val form = LinkedMultiValueMap<String, String>().apply {
            add("client_id", config.clientId)
            add("client_secret", clientSecret)
            add("token", token)
            add("token_type_hint", if (tokens.refreshToken != null) "refresh_token" else "access_token")
        }
        restClient.post()
            .uri(config.revokeUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .toBodilessEntity()
    }

    private fun exchangeAuthorizationCode(code: String, clientSecret: String): AppleTokenResponse {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "authorization_code")
            add("code", code)
            add("client_id", config.clientId)
            add("client_secret", clientSecret)
        }
        return restClient.post()
            .uri(config.tokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .body(AppleTokenResponse::class.java)
            ?: error("Apple token exchange returned an empty body")
    }

    data class AppleTokenResponse(
        @JsonProperty("access_token") val accessToken: String? = null,
        @JsonProperty("refresh_token") val refreshToken: String? = null
    )
}
