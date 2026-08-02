package com.challa.web.upload

import com.challa.core.upload.IssuePhotoUploadCommand
import com.challa.core.upload.IssuePhotoUploadResult
import com.challa.core.upload.IssuePhotoUploadUseCase
import com.challa.core.upload.IssueUploadUrlCommand
import com.challa.core.upload.IssueUploadUrlUseCase
import com.challa.core.upload.NoRemainedPhotoException
import com.challa.core.upload.UploadPurpose
import com.challa.core.upload.UploadUrl
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

class UploadControllerTest {
    private val issueUploadUrlUseCase = mockk<IssueUploadUrlUseCase>()
    private val issuePhotoUploadUseCase = mockk<IssuePhotoUploadUseCase>()
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(UploadController(issueUploadUrlUseCase, issuePhotoUploadUseCase))
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `PROFILE_IMAGE keeps the existing request and response`() {
        every {
            issueUploadUrlUseCase.issue(
                USER_ID,
                IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "image/jpeg")
            )
        } returns uploadUrl()

        mockMvc.perform(
            post("/api/v1/uploads").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"upload":{"purpose":"PROFILE_IMAGE","contentType":"image/jpeg"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.upload.uploadUrl").value("https://bucket/profile?signature"))
            .andExpect(jsonPath("$.data.upload.imageUrl").value("https://bucket/profile"))
    }

    @Test
    fun `PHOTO directs clients to the dedicated endpoint`() {
        mockMvc.perform(
            post("/api/v1/uploads").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"upload":{"purpose":"PHOTO","contentType":"image/jpeg"}}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Use POST /api/v1/uploads/photos for PHOTO"))

        verify(exactly = 0) { issueUploadUrlUseCase.issue(any(), any()) }
    }

    @Test
    fun `photo upload URL issuance accepts a flat request`() {
        val command = IssuePhotoUploadCommand(
            userId = USER_ID,
            roomId = ROOM_ID,
            cameraFilterId = "filter-original",
            contentType = "image/jpeg"
        )
        every { issuePhotoUploadUseCase.issue(command) } returns IssuePhotoUploadResult(
            photoId = PHOTO_ID,
            uploadUrl = "https://bucket/photo?signature",
            imageUrl = "https://bucket/photo",
            expiresInSeconds = 300,
            remainedPhotoCount = 18
        )

        mockMvc.perform(
            post("/api/v1/uploads/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"roomId":11,"cameraFilterId":"filter-original","contentType":"image/jpeg"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.upload.uploadUrl").value("https://bucket/photo?signature"))
            .andExpect(jsonPath("$.data.upload.imageUrl").value("https://bucket/photo"))
            .andExpect(jsonPath("$.data.photo.id").value(PHOTO_ID))
            .andExpect(jsonPath("$.data.photo.status").doesNotExist())
            .andExpect(jsonPath("$.data.room.remainedPhotoCount").value(18))

        verify { issuePhotoUploadUseCase.issue(command) }
    }

    @Test
    fun `an exhausted room returns conflict without a URL`() {
        every { issuePhotoUploadUseCase.issue(any()) } throws NoRemainedPhotoException()

        mockMvc.perform(
            post("/api/v1/uploads/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"roomId":11,"cameraFilterId":"filter-original","contentType":"image/jpeg"}""")
        )
            .andExpect(status().isConflict)
            .andExpect(jsonPath("$.message").value("촬영 가능한 장 수가 없습니다"))
    }

    private fun uploadUrl() = UploadUrl(
        uploadUrl = "https://bucket/profile?signature",
        imageUrl = "https://bucket/profile",
        expiresInSeconds = 300
    )

    private fun <B : MockHttpServletRequestBuilder> B.authenticated(): B = apply {
        requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, USER_ID)
    }

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
        const val PHOTO_ID = 31L
    }
}
