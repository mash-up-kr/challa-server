package com.challa.web.room

import com.challa.core.room.application.InvitationCodeAllocationFailedException
import com.challa.core.room.application.InvitationCodeNotFoundException
import com.challa.core.room.application.NoMatchingRoomException
import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.*
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDateTime

class RoomControllerTest {
    private val createRoomUsecase = mockk<CreateRoomUsecase>()
    private val joinRoomUsecase = mockk<JoinRoomUsecase>(relaxed = true)
    private val listRoomsUsecase = mockk<ListRoomsUsecase>()
    private val getRoomUsecase = mockk<GetRoomUsecase>()
    private val getShootableRoomsUsecase = mockk<GetShootableRoomsUsecase>()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(
            RoomController(
                createRoomUsecase = createRoomUsecase,
                joinRoomUsecase = joinRoomUsecase,
                listRoomsUsecase = listRoomsUsecase,
                getRoomUsecase = getRoomUsecase,
                getShootableRoomsUsecase = getShootableRoomsUsecase
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
                    totalPhotoCount = 24
                )
            )
        } returns CreateRoomResult(id = 1)

        mockMvc.perform(
            post("/api/v1/rooms")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"room":{"title":"Trip","totalPhotoCount":24}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.room.id").value(1))

        verify {
            createRoomUsecase.createRoom(
                CreateRoomCommand(
                    userId = AUTH_USER_ID,
                    roomTitle = "Trip",
                    totalPhotoCount = 24
                )
            )
        }
    }

    @Test
    fun `GET rooms uses the authenticated user ID`() {
        every {
            listRoomsUsecase.listRooms(
                ListRoomsCommand(userId = AUTH_USER_ID, status = listOf(RoomStatus.SHOOTING))
            )
        } returns ListRoomsResult(
            roomProjections = listOf(
                ListRoomsResult.RoomProjection(
                    roomId = 11L,
                    roomStatus = RoomStatus.SHOOTING,
                    title = "Trip",
                    memberCount = 3L,
                    remainedPhotoCount = 24L
                )
            )
        )

        mockMvc.perform(get("/api/v1/rooms").param("status", "SHOOTING").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.room[0].id").value(11))
            .andExpect(jsonPath("$.data.room[0].status").value("SHOOTING"))
            .andExpect(jsonPath("$.data.room[0].remainedPhotoCount").value(24))

        verify {
            listRoomsUsecase.listRooms(
                ListRoomsCommand(userId = AUTH_USER_ID, status = listOf(RoomStatus.SHOOTING))
            )
        }
    }

    @Test
    fun `GET room uses the authenticated user ID`() {
        val room = Room(
            id = 11L,
            title = "Trip",
            totalPhotoCount = 24L,
            remainedPhotoCount = 24L,
            invitationCode = "123456",
            roomStatus = RoomStatus.SHOOTING,
            photoPrintCompletionAt = null,
            createdAt = LocalDateTime.of(2026, 8, 1, 12, 0)
        )
        every {
            getRoomUsecase.getRoom(GetRoomCommand(userId = AUTH_USER_ID, roomId = 11L))
        } returns GetRoomResult(room)

        mockMvc.perform(get("/api/v1/rooms/11").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.room.id").value(11))
            .andExpect(jsonPath("$.data.room.title").value("Trip"))
            .andExpect(jsonPath("$.data.room.status").value("SHOOTING"))
            .andExpect(jsonPath("$.data.room.totalPhotoCount").value(24))
            .andExpect(jsonPath("$.data.room.remainedPhotoCount").value(24))

        verify { getRoomUsecase.getRoom(GetRoomCommand(userId = AUTH_USER_ID, roomId = 11L)) }
    }

    @Test
    fun `room not visible to the user returns 404`() {
        every {
            getRoomUsecase.getRoom(GetRoomCommand(userId = AUTH_USER_ID, roomId = 11L))
        } throws NoMatchingRoomException()

        mockMvc.perform(get("/api/v1/rooms/11").authenticated())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Room not found"))
    }

    @Test
    fun `POST join uses the authenticated user ID`() {
        every {
            joinRoomUsecase.joinRoom(
                JoinRoomCommand(
                    userId = AUTH_USER_ID,
                    invitationCode = "123456"
                )
            )
        } returns JoinRoomResult(id = 1L)

        mockMvc.perform(
            post("/api/v1/rooms/join")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"room":{"invitationCode":"123456"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.room.id").value(1))

        verify {
            joinRoomUsecase.joinRoom(
                JoinRoomCommand(
                    userId = AUTH_USER_ID,
                    invitationCode = "123456"
                )
            )
        }
    }

    @Test
    fun `unknown invitation code returns 404`() {
        every { joinRoomUsecase.joinRoom(any()) } throws InvitationCodeNotFoundException()

        mockMvc.perform(
            post("/api/v1/rooms/join")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"room":{"invitationCode":"999999"}}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Room not found"))
    }

    @Test
    fun `room API requires authentication`() {
        mockMvc.perform(
            post("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"room":{"title":"Trip","totalPhotoCount":24}}""")
        )
            .andExpect(status().isUnauthorized)
            .andExpect(jsonPath("$.message").value("Authentication required"))
    }

    @Test
    fun `invitation code allocation failure returns 503`() {
        every { createRoomUsecase.createRoom(any()) } throws InvitationCodeAllocationFailedException()

        mockMvc.perform(
            post("/api/v1/rooms")
                .authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"room":{"title":"Trip","totalPhotoCount":24}}""")
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
