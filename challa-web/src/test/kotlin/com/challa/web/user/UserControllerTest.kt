package com.challa.web.user

import com.challa.core.auth.Provider
import com.challa.core.user.DeleteAccountUseCase
import com.challa.core.user.GetProfileUseCase
import com.challa.core.user.InvalidNicknameException
import com.challa.core.user.UpdateProfileCommand
import com.challa.core.user.UpdateProfileUseCase
import com.challa.core.user.User
import com.challa.core.user.UserNotFoundException
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class UserControllerTest {
    private val getProfile = mockk<GetProfileUseCase>()
    private val updateProfile = mockk<UpdateProfileUseCase>()
    private val deleteAccount = mockk<DeleteAccountUseCase>(relaxed = true)

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(UserController(getProfile, updateProfile, deleteAccount))
        .setCustomArgumentResolvers(AuthUserIdArgumentResolver())
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `GET me returns the profile`() {
        every { getProfile.getProfile(7) } returns user(nickname = "호랑이")

        mockMvc.perform(get("/api/v1/users/me").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.user.id").value(7))
            .andExpect(jsonPath("$.data.user.nickname").value("호랑이"))
    }

    @Test
    fun `GET me returns a null nickname before onboarding`() {
        every { getProfile.getProfile(7) } returns user(nickname = null)

        mockMvc.perform(get("/api/v1/users/me").authenticated())
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.user.nickname").value(null as String?))
    }

    @Test
    fun `PUT me replaces the whole profile`() {
        every { updateProfile.update(7, UpdateProfileCommand("호랑이", "https://img.example/1.png")) } returns
            user(nickname = "호랑이", profileImageUrl = "https://img.example/1.png")

        mockMvc.perform(
            put("/api/v1/users/me").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"user":{"nickname":"호랑이","profileImageUrl":"https://img.example/1.png"}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.user.nickname").value("호랑이"))
            .andExpect(jsonPath("$.data.user.profileImageUrl").value("https://img.example/1.png"))

        verify { updateProfile.update(7, UpdateProfileCommand("호랑이", "https://img.example/1.png")) }
    }

    @Test
    fun `PUT me clears the image on an explicit null`() {
        every { updateProfile.update(7, UpdateProfileCommand("호랑이", null)) } returns
            user(nickname = "호랑이", profileImageUrl = null)

        mockMvc.perform(
            put("/api/v1/users/me").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"user":{"nickname":"호랑이","profileImageUrl":null}}""")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.user.profileImageUrl").value(null as String?))
    }

    @Test
    fun `PUT me rejects a partial body with 400`() {
        listOf(
            """{"user":{"nickname":"호랑이"}}""",
            """{"user":{"profileImageUrl":null}}""",
            """{"user":{}}""",
            """{"nickname":"호랑이","profileImageUrl":null}""",
            "{}"
        ).forEach { body ->
            mockMvc.perform(
                put("/api/v1/users/me").authenticated()
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            )
                .andExpect(status().isBadRequest)
                .andExpect(jsonPath("$.success").value(false))
        }
    }

    @Test
    fun `PUT me surfaces a rejected nickname as 400 with the rule that failed`() {
        every { updateProfile.update(7, any()) } throws
            InvalidNicknameException("Nickname must not be blank")

        mockMvc.perform(
            put("/api/v1/users/me").authenticated()
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"user":{"nickname":"   ","profileImageUrl":null}}""")
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value("Nickname must not be blank"))
    }

    @Test
    fun `PUT me requires authentication`() {
        mockMvc.perform(
            put("/api/v1/users/me")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""{"user":{"nickname":"호랑이","profileImageUrl":null}}""")
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `DELETE me withdraws the account`() {
        mockMvc.perform(delete("/api/v1/users/me").authenticated())
            .andExpect(status().isOk)

        verify { deleteAccount.delete(7) }
    }

    @Test
    fun `an unknown user is a 404`() {
        every { getProfile.getProfile(7) } throws UserNotFoundException()

        mockMvc.perform(get("/api/v1/users/me").authenticated())
            .andExpect(status().isNotFound)
    }

    private fun user(nickname: String?, profileImageUrl: String? = null) = User(
        id = 7,
        provider = Provider.KAKAO,
        providerId = "kakao-sub",
        nickname = nickname,
        profileImageUrl = profileImageUrl
    )

    private fun <B : org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder> B.authenticated(): B =
        apply {
            requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, 7L)
        }
}
