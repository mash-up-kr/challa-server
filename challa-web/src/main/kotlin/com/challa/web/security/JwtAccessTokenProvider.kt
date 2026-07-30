package com.challa.web.security

import com.challa.core.domain.Provider
import com.challa.core.exception.InvalidTokenException
import com.challa.core.port.outbound.AccessTokenIssuer
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.time.Clock
import java.util.Date

@Component
class JwtAccessTokenProvider(properties: JwtProperties, private val clock: Clock) : AccessTokenIssuer {
    private val issuer = properties.issuer
    private val accessTokenTtl = properties.accessTokenTtl
    private val signingKey = Keys.hmacShaKeyFor(properties.secret.toByteArray(StandardCharsets.UTF_8))

    override fun issue(userId: Long, provider: Provider): String {
        val now = clock.instant()
        return Jwts.builder()
            .issuer(issuer)
            .subject(userId.toString())
            .claim(PROVIDER_CLAIM, provider.name)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plus(accessTokenTtl)))
            .signWith(signingKey)
            .compact()
    }

    fun parse(token: String): AuthPrincipal {
        val claims = try {
            Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(issuer)
                .clock { Date.from(clock.instant()) }
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (ex: JwtException) {
            throw InvalidTokenException("Invalid access token: ${ex.message}")
        } catch (ex: IllegalArgumentException) {
            throw InvalidTokenException("Invalid access token: ${ex.message}")
        }

        val userId = claims.subject?.toLongOrNull()
            ?: throw InvalidTokenException("Access token has no valid subject")
        return AuthPrincipal(userId = userId, provider = claims[PROVIDER_CLAIM]?.toString())
    }

    companion object {
        private const val PROVIDER_CLAIM = "provider"
    }
}

data class AuthPrincipal(val userId: Long, val provider: String?)
