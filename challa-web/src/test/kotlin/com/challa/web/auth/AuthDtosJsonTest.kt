package com.challa.web.auth

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import tools.jackson.module.kotlin.jacksonObjectMapper

class AuthDtosJsonTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `login response exposes the flag as isNew`() {
        val json = mapper.writeValueAsString(LoginResponse("access", "refresh", isNew = true))

        assertEquals("""{"accessToken":"access","refreshToken":"refresh","isNew":true}""", json)
    }
}
