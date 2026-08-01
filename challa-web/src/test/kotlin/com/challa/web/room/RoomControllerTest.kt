package com.challa.web.room

import com.challa.core.room.application.InviteCodeAllocationFailedException
import com.challa.core.room.application.InviteCodeNotFoundException
import com.challa.core.room.port.input.CreateRoomCommand
import com.challa.core.room.port.input.CreateRoomResult
import com.challa.core.room.port.input.CreateRoomUsecase
import com.challa.core.room.port.input.JoinRoomCommand
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.web.common.exception.GlobalExceptionHandler
import com.challa.web.security.AuthUserIdArgumentResolver
import com.challa.web.security.AuthenticationInterceptor
import com.challa.web.security.JwtAuthenticationFilter
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class RoomControllerTest {
    private val createRoomUsecase = mockk<CreateRoomUsecase>()
    private val joinRoomUsecase = mockk<JoinRoomUsecase>(relaxed = true)

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(
            RoomController(
                createRoomUsecase = createRoomUsecase,
                joinRoomUsecase = joinRoomUsecase
            )
        )
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `POST create uses the authenticated user ID`() {
        every {
            createRoomUsecase.createRoom(
                CreateRoomCommand(
                    userId = AUTH_USER_ID,
                    roomTitle = "Trip",
                    filmLimit = 24
                )
            )
        } returns CreateRoomResult(inviteCode = "123456")

        mockMvc.perform(
            post("/api/v1/rooms")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"roomTitle":"Trip","filmLimit":24}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.inviteCode").value("123456"))

        verify {
            createRoomUsecase.createRoom(
                CreateRoomCommand(
                    userId = AUTH_USER_ID,
                    roomTitle = "Trip",
                    filmLimit = 24
                )
            )
        }
    }

    @Test
    fun `POST join uses the authenticated user ID`() {
        mockMvc.perform(
            post("/api/v1/rooms/join")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"inviteCode":"123456"}""")
        )
            .andExpect(status().isOk)

        verify {
            joinRoomUsecase.joinRoom(
                JoinRoomCommand(
                    userId = AUTH_USER_ID,
                    inviteCode = "123456"
                )
            )
        }
    }

    @Test
    fun `unknown invite code returns 404`() {
        every { joinRoomUsecase.joinRoom(any()) } throws InviteCodeNotFoundException()

        mockMvc.perform(
            post("/api/v1/rooms/join")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"inviteCode":"999999"}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Room not found"))
    }

    @Test
    fun `room API requires authentication`() {
        mockMvc.perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"roomTitle":"Trip","filmLimit":24}""")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Authentication required"))
    }

    @Test
    fun `invite code allocation failure returns 503`() {
        every { createRoomUsecase.createRoom(any()) } throws InviteCodeAllocationFailedException()

        mockMvc.perform(
            post("/api/v1/rooms")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"roomTitle":"Trip","filmLimit":24}""")
        )
            .andExpect(status().isServiceUnavailable)
            .andExpect(jsonPath("$.message").value("Room creation temporarily unavailable"))
    }

    private fun <B : MockHttpServletRequestBuilder> B.authenticated(): B = apply {
        requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, AUTH_USER_ID)
    }

    companion object {
        private const val AUTH_USER_ID = 7L
    }
}
