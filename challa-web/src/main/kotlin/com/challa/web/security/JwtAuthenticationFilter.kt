package com.challa.web.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(private val jwtAccessTokenProvider: JwtAccessTokenProvider) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        bearerToken(request)?.let { token ->
            runCatching { jwtAccessTokenProvider.parse(token) }
                .onSuccess { principal ->
                    request.setAttribute(AUTH_USER_ID_ATTRIBUTE, principal.userId)
                }
                .onFailure { ex ->

                    log.warn("Bearer token rejected: {}", ex.message)
                }
        }
        filterChain.doFilter(request, response)
    }

    private fun bearerToken(request: HttpServletRequest): String? {
        val header = request.getHeader(HttpHeaders.AUTHORIZATION) ?: return null
        return header.takeIf { it.startsWith(BEARER_PREFIX, ignoreCase = true) }
            ?.substring(BEARER_PREFIX.length)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }

    companion object {
        const val AUTH_USER_ID_ATTRIBUTE = "com.challa.web.authUserId"
        private const val BEARER_PREFIX = "Bearer "
    }
}
