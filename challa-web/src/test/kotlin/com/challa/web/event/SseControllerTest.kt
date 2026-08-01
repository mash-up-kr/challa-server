package com.challa.web.event

import com.challa.web.common.exception.GlobalExceptionHandler
import com.challa.web.security.AuthUserIdArgumentResolver
import com.challa.web.security.AuthenticationInterceptor
import com.challa.web.security.JwtAuthenticationFilter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.request
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

class SseControllerTest {
    private val sseConnectionService = mockk<SseConnectionService>()
    private val mockMvc = MockMvcBuilders
        .standaloneSetup(SseController(sseConnectionService))
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .build()

    @Test
    fun `GET events uses the authenticated user ID`() {
        every { sseConnectionService.subscribe(AUTH_USER_ID) } returns SseEmitter()

        mockMvc.perform(
            get("/api/v1/sse/events")
                .requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, AUTH_USER_ID)
        )
            .andExpect(status().isOk)
            .andExpect(request().asyncStarted())

        verify { sseConnectionService.subscribe(AUTH_USER_ID) }
    }

    @Test
    fun `SSE API requires authentication`() {
        mockMvc.perform(get("/api/v1/sse/events"))
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Authentication required"))
    }

    companion object {
        private const val AUTH_USER_ID = 7L
    }
}
