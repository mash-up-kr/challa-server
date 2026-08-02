package com.challa.web.photo

import com.challa.core.photo.CompletePhotoCommand
import com.challa.core.photo.CompletePhotoResult
import com.challa.core.photo.CompletePhotoUseCase
import com.challa.core.photo.NoRemainedPhotoException
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

class PhotoControllerTest {
    private val completePhotoUseCase = mockk<CompletePhotoUseCase>()
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(PhotoController(completePhotoUseCase))
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
        const val CAMERA_FILTER_NAME = "filter-original"
        const val IMAGE_URL = "https://bucket/photo/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
    }
}
