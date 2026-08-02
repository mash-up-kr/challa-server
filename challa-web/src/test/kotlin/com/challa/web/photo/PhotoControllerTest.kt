package com.challa.web.photo

import com.challa.core.photo.*
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
    fun `stores the image URL in the reserved photo`() {
        val command = CompletePhotoCommand(USER_ID, PHOTO_ID, IMAGE_URL)
        every { completePhotoUseCase.complete(command) } returns CompletePhotoResult(photo())

        mockMvc.perform(
            post("/api/v1/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"photoId":31,"imageUrl":"$IMAGE_URL"}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data").isEmpty())

        verify { completePhotoUseCase.complete(command) }
    }

    @Test
    fun `returns not found when the reserved photo does not belong to the user`() {
        every { completePhotoUseCase.complete(any()) } throws PhotoNotFoundException()

        mockMvc.perform(
            post("/api/v1/photos").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"photoId":31,"imageUrl":"$IMAGE_URL"}""")
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.message").value("Photo not found"))
    }

    private fun photo() = Photo(
        id = PHOTO_ID,
        roomId = 11,
        userId = USER_ID,
        imageUrl = IMAGE_URL,
        filterId = "filter-original"
    )

    private fun <B : MockHttpServletRequestBuilder> B.authenticated(): B = apply {
        requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, USER_ID)
    }

    private companion object {
        const val USER_ID = 7L
        const val PHOTO_ID = 31L
        const val IMAGE_URL = "https://bucket/photo"
    }
}
