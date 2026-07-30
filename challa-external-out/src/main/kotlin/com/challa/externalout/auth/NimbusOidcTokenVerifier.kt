package com.challa.externalout.auth

import com.challa.core.auth.InvalidTokenException
import com.challa.core.auth.OidcIdentity
import com.challa.core.auth.OidcTokenVerifier
import com.challa.core.auth.Provider
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.jwk.source.JWKSourceBuilder
import com.nimbusds.jose.proc.JWSVerificationKeySelector
import com.nimbusds.jose.proc.SecurityContext
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier
import com.nimbusds.jwt.proc.DefaultJWTProcessor
import org.springframework.stereotype.Component
import java.net.URI

@Component
class NimbusOidcTokenVerifier(properties: OidcProperties) : OidcTokenVerifier {
    private val configs: Map<Provider, ProviderOidc> = mapOf(
        Provider.KAKAO to properties.kakao,
        Provider.APPLE to properties.apple
    )

    private val processors: Map<Provider, ConfigurableJWTProcessor<SecurityContext>> =
        configs.mapValues { (_, cfg) -> buildProcessor(cfg) }

    override fun verify(provider: Provider, idToken: String): OidcIdentity {
        val claims = try {
            processors.getValue(provider).process(idToken, null)
        } catch (ex: Exception) {
            throw InvalidTokenException("id_token verification failed: ${ex.message}")
        }

        val subject = claims.subject
            ?: throw InvalidTokenException("id_token has no subject")
        return OidcIdentity(
            provider = provider,
            subject = subject,

            profileImageUrl = claims.getStringClaimOrNull("picture")
        )
    }

    private fun buildProcessor(cfg: ProviderOidc): ConfigurableJWTProcessor<SecurityContext> {
        val processor = DefaultJWTProcessor<SecurityContext>()
        val jwkSource = JWKSourceBuilder.create<SecurityContext>(URI(cfg.jwksUri).toURL()).build()
        processor.jwsKeySelector = JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource)

        val acceptedAudiences = listOfNotNull(cfg.clientId, cfg.nativeClientId)
            .filter { it.isNotBlank() }
            .toSet()
        processor.jwtClaimsSetVerifier = DefaultJWTClaimsVerifier(
            acceptedAudiences,
            JWTClaimsSet.Builder().issuer(cfg.issuer).build(),
            setOf("sub", "exp"),
            null
        )
        return processor
    }

    private fun JWTClaimsSet.getStringClaimOrNull(name: String): String? = runCatching {
        getStringClaim(name)
    }.getOrNull()?.takeIf { it.isNotBlank() }
}
