package com.challa.web.auth

import com.challa.core.auth.Provider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import tools.jackson.core.JacksonException
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

class AuthDtosJsonTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `login response nests the payload under auth and exposes the flag as isNew`() {
        val json = mapper.writeValueAsString(AuthEnvelope(LoginResponse("access", "refresh", isNew = true)))

        assertEquals("""{"auth":{"accessToken":"access","refreshToken":"refresh","isNew":true}}""", json)
    }

    @Test
    fun `login request reads the payload from under auth`() {
        val request: AuthEnvelope<LoginRequest> =
            mapper.readValue("""{"auth":{"provider":"KAKAO","idToken":"token"}}""")

        assertEquals(Provider.KAKAO, request.auth.provider)
        assertEquals("token", request.auth.idToken)
    }

    @Test
    fun `login request rejects a body that is not wrapped in auth`() {
        assertThrows(JacksonException::class.java) {
            mapper.readValue<AuthEnvelope<LoginRequest>>("""{"provider":"KAKAO","idToken":"token"}""")
        }
    }
}
