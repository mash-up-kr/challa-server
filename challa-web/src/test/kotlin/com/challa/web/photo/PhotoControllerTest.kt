package com.challa.web.photo

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.photo.*
import com.challa.core.room.application.NoMatchingRoomException
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

class PhotoControllerTest {
    private val completePhotoUseCase = mockk<CompletePhotoUseCase>()
    private val listPhotosUseCase = mockk<ListPhotosUseCase>()
    private val getPhotoDetailUseCase = mockk<GetPhotoDetailUseCase>()
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(PhotoController(completePhotoUseCase, listPhotosUseCase, getPhotoDetailUseCase))
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `returns only the remaining photo count after completion`() {
        val command = command()
        every { completePhotoUseCase.complete(command) } returns CompletePhotoResult(remainedPhotoCount = 18)

        mockMvc.perform(
            post("/api/v1/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.remainedPhotoCount").value(18))
            .andExpect(jsonPath("$.data.photoId").doesNotExist())

        verify { completePhotoUseCase.complete(command) }
    }

    @Test
    fun `returns conflict when no photos remain`() {
        every { completePhotoUseCase.complete(any()) } throws NoRemainedPhotoException()

        mockMvc.perform(
            post("/api/v1/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody())
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("촬영 가능한 장 수가 없습니다"))
    }

    @Test
    fun `returns all photos from a room the user participates in`() {
        val command = ListPhotosCommand(userId = USER_ID, roomId = ROOM_ID)
        every { listPhotosUseCase.listPhotos(command) } returns ListPhotosResult(
            photoProjections = listOf(
                ListPhotosResult.PhotoProjection(id = PHOTO_ID, imageUrl = IMAGE_URL)
            )
        )

        mockMvc.perform(get("/api/v1/photos").param("roomId", ROOM_ID.toString()).authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.photoProjections[0].id").value(PHOTO_ID))
            .andExpect(jsonPath("$.data.photoProjections[0].imageUrl").value(IMAGE_URL))

        verify { listPhotosUseCase.listPhotos(command) }
    }

    @Test
    fun `returns not found when listing photos from a room the user does not participate in`() {
        every { listPhotosUseCase.listPhotos(any()) } throws NoMatchingRoomException()

        mockMvc.perform(get("/api/v1/photos").param("roomId", ROOM_ID.toString()).authenticated())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Room not found"))
    }

    @Test
    fun `returns an owned photo with its chats`() {
        val command = GetPhotoDetailCommand(userId = USER_ID, photoId = PHOTO_ID)
        val chat = Chat(
            id = 41,
            type = ChatType.COMMENT,
            content = "멋진 사진",
            photoId = PHOTO_ID,
            roomId = ROOM_ID,
            userId = 8
        )
        every { getPhotoDetailUseCase.getPhotoDetail(command) } returns GetPhotoDetailResult(
            photoDetail = PhotoDetail(id = PHOTO_ID, chats = listOf(chat))
        )

        mockMvc.perform(get("/api/v1/photos/$PHOTO_ID").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.photoDetail.id").value(PHOTO_ID))
            .andExpect(jsonPath("$.data.photoDetail.chats[0].id").value(41))
            .andExpect(jsonPath("$.data.photoDetail.chats[0].content").value("멋진 사진"))

        verify { getPhotoDetailUseCase.getPhotoDetail(command) }
    }

    @Test
    fun `returns not found when the detailed photo does not belong to the user`() {
        every { getPhotoDetailUseCase.getPhotoDetail(any()) } throws PhotoNotFoundException()

        mockMvc.perform(get("/api/v1/photos/$PHOTO_ID").authenticated())
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Photo not found"))
    }

    private fun command() = CompletePhotoCommand(
        userId = USER_ID,
        roomId = ROOM_ID,
        cameraFilterName = CAMERA_FILTER_NAME,
        imageUrl = IMAGE_URL
    )

    private fun requestBody() = """{"roomId":11,"cameraFilterName":"$CAMERA_FILTER_NAME","imageUrl":"$IMAGE_URL"}"""

    private fun <B : MockHttpServletRequestBuilder> B.authenticated(): B = apply {
        requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, USER_ID)
    }

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val PHOTO_ID = 31L
        const val CAMERA_FILTER_NAME = "filter-original"
        const val IMAGE_URL = "https://bucket/photo/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
    }
}
