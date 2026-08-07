package com.challa.web.room

import com.challa.core.room.application.InvitationCodeAllocationFailedException
import com.challa.core.room.application.InvitationCodeNotFoundException
import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.exception.NoMatchingRoomException
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
    private val getRoomUsersUsecase = mockk<GetRoomUsersUsecase>()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(
            RoomController(
                createRoomUsecase = createRoomUsecase,
                joinRoomUsecase = joinRoomUsecase,
                listRoomsUsecase = listRoomsUsecase,
                getRoomUsecase = getRoomUsecase,
                getShootableRoomsUsecase = getShootableRoomsUsecase,
                getRoomUsersUsecase = getRoomUsersUsecase
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
                    totalPhotoCount = 30L,
                    remainedPhotoCount = 24L,
                    thumbnailImageUrls = listOf(
                        "https://bucket/photo/11/7/first",
                        "https://bucket/photo/11/8/second"
                    ),
                    photoPrintCompletionAt = null
                )
            )
        )

        mockMvc.perform(get("/api/v1/rooms").param("status", "SHOOTING").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.rooms[0].id").value(11))
            .andExpect(jsonPath("$.data.rooms[0].status").value("SHOOTING"))
            .andExpect(jsonPath("$.data.rooms[0].totalPhotoCount").value(30))
            .andExpect(jsonPath("$.data.rooms[0].remainedPhotoCount").value(24))
            .andExpect(jsonPath("$.data.rooms[0].thumbnailImageUrls[0]").value("https://bucket/photo/11/7/first"))
            .andExpect(jsonPath("$.data.rooms[0].thumbnailImageUrls[1]").value("https://bucket/photo/11/8/second"))
            .andExpect(jsonPath("$.data.rooms[0].photoPrintCompletedAt").isEmpty)
            .andExpect(jsonPath("$.data.room").doesNotExist())

        verify {
            listRoomsUsecase.listRooms(
                ListRoomsCommand(userId = AUTH_USER_ID, status = listOf(RoomStatus.SHOOTING))
            )
        }
    }

    @Test
    fun `GET shootable rooms returns rooms envelope`() {
        every {
            getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = AUTH_USER_ID))
        } returns GetShootableRoomsResult(
            rooms = listOf(
                GetShootableRoomsResult.ShootableRoom(
                    id = 11L,
                    title = "Trip",
                    remainedPhotoCount = 24L,
                    totalPhotoCount = 30L
                )
            )
        )

        mockMvc.perform(get("/api/v1/rooms/shootable").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.rooms[0].id").value(11))
            .andExpect(jsonPath("$.data.rooms[0].title").value("Trip"))
            .andExpect(jsonPath("$.data.rooms[0].remainedPhotoCount").value(24))
            .andExpect(jsonPath("$.data.rooms[0].totalPhotoCount").value(30))
            .andExpect(jsonPath("$.data.room").doesNotExist())

        verify {
            getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = AUTH_USER_ID))
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
    fun `GET room users uses the authenticated user ID and maps the users`() {
        every {
            getRoomUsersUsecase.getRoomUsers(GetRoomUsersCommand(userId = AUTH_USER_ID, roomId = 11L))
        } returns GetRoomUsersResult(
            userProjections = listOf(
                GetRoomUsersResult.UserProjection(
                    id = 8L,
                    nickname = "라이언",
                    profileImageUrl = "https://img.example/ryan.png"
                ),
                GetRoomUsersResult.UserProjection(id = 9L, nickname = null, profileImageUrl = null)
            )
        )

        mockMvc.perform(get("/api/v1/rooms/11/users").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.users[0].id").value(8))
            .andExpect(jsonPath("$.data.users[0].nickname").value("라이언"))
            .andExpect(jsonPath("$.data.users[0].profileImageUrl").value("https://img.example/ryan.png"))
            .andExpect(jsonPath("$.data.users[1].id").value(9))
            .andExpect(jsonPath("$.data.users[1].nickname").isEmpty)
            .andExpect(jsonPath("$.data.users[1].profileImageUrl").isEmpty)
            .andExpect(jsonPath("$.data.room").doesNotExist())

        verify {
            getRoomUsersUsecase.getRoomUsers(GetRoomUsersCommand(userId = AUTH_USER_ID, roomId = 11L))
        }
    }

    @Test
    fun `GET room users returns 404 when the requester is not in the room`() {
        every {
            getRoomUsersUsecase.getRoomUsers(GetRoomUsersCommand(userId = AUTH_USER_ID, roomId = 11L))
        } throws NoMatchingRoomException()

        mockMvc.perform(get("/api/v1/rooms/11/users").authenticated())
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
