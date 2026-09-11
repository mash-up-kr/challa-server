package com.challa.web.upload

import com.challa.core.upload.IssueUploadUrlCommand
import com.challa.core.upload.IssueUploadUrlUseCase
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
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(UploadController(issueUploadUrlUseCase))
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `PROFILE_IMAGE keeps the existing upload endpoint`() {
        val command = IssueUploadUrlCommand(UploadPurpose.PROFILE_IMAGE, "image/jpeg")
        every { issueUploadUrlUseCase.issue(USER_ID, command) } returns uploadUrl(PROFILE_IMAGE_KEY)

        mockMvc.perform(
            post("/api/v1/uploads").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"upload":{"purpose":"PROFILE_IMAGE","contentType":"image/jpeg"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.upload.uploadUrl").value("https://bucket/$PROFILE_IMAGE_KEY?signature"))
            .andExpect(jsonPath("$.data.upload.imageUrl").value("https://bucket/$PROFILE_IMAGE_KEY"))

        verify { issueUploadUrlUseCase.issue(USER_ID, command) }
    }

    @Test
    fun `PHOTO uses the common upload endpoint`() {
        val command = IssueUploadUrlCommand(UploadPurpose.PHOTO, "image/jpeg")
        every { issueUploadUrlUseCase.issue(USER_ID, command) } returns
            uploadUrl(PHOTO_IMAGE_KEY, PHOTO_THUMBNAIL_IMAGE_KEY)

        mockMvc.perform(
            post("/api/v1/uploads").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"upload":{"purpose":"PHOTO","contentType":"image/jpeg"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.upload.uploadUrl").value("https://bucket/$PHOTO_IMAGE_KEY?signature"))
            .andExpect(jsonPath("$.data.upload.imageUrl").value("https://bucket/$PHOTO_IMAGE_KEY"))
            .andExpect(
                jsonPath("$.data.upload.thumbnailUploadUrl")
                    .value("https://bucket/$PHOTO_THUMBNAIL_IMAGE_KEY?signature")
            )
            .andExpect(
                jsonPath("$.data.upload.thumbnailImageUrl").value("https://bucket/$PHOTO_THUMBNAIL_IMAGE_KEY")
            )

        verify { issueUploadUrlUseCase.issue(USER_ID, command) }
    }

    private fun uploadUrl(objectKey: String, thumbnailObjectKey: String? = null) = UploadUrl(
        uploadUrl = "https://bucket/$objectKey?signature",
        imageUrl = "https://bucket/$objectKey",
        expiresInSeconds = 300,
        thumbnailUploadUrl = thumbnailObjectKey?.let { "https://bucket/$it?signature" },
        thumbnailImageUrl = thumbnailObjectKey?.let { "https://bucket/$it" }
    )

    private fun <B : MockHttpServletRequestBuilder> B.authenticated(): B = apply {
        requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, USER_ID)
    }

    private companion object {
        const val USER_ID = 7L
        const val PROFILE_IMAGE_KEY = "profile/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
        const val PHOTO_IMAGE_KEY = "photo/7/92f48652-0c77-4fde-bc95-f7e09669b40e"
        const val PHOTO_THUMBNAIL_IMAGE_KEY = "${PHOTO_IMAGE_KEY}_thumbnail"
    }
}
