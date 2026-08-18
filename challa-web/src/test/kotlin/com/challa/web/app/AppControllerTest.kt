package com.challa.web.app

import com.challa.core.app.domain.AppOs
import com.challa.core.app.port.input.GetAppVersionCommand
import com.challa.core.app.port.input.GetAppVersionResult
import com.challa.core.app.port.input.GetAppVersionUsecase
import com.challa.web.common.exception.GlobalExceptionHandler
import com.challa.web.security.AuthenticationInterceptor
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

class AppControllerTest {
    private val getAppVersionUsecase = mockk<GetAppVersionUsecase>()

    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(AppController(getAppVersionUsecase))
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `GET app version is public and returns the app envelope`() {
        every {
            getAppVersionUsecase.getAppVersion(
                GetAppVersionCommand(os = AppOs.ANDROID, version = "1.1.0")
            )
        } returns GetAppVersionResult(
            updateRequired = true,
            updateAvailable = true,
            latestVersion = "1.5.0",
            storeUrl = STORE_URL
        )

        mockMvc.perform(
            get("/api/v1/app/version")
                .param("os", "ANDROID")
                .param("version", "1.1.0")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("OK"))
            .andExpect(jsonPath("$.data.app.updateRequired").value(true))
            .andExpect(jsonPath("$.data.app.updateAvailable").value(true))
            .andExpect(jsonPath("$.data.app.latestVersion").value("1.5.0"))
            .andExpect(jsonPath("$.data.app.storeUrl").value(STORE_URL))
            .andExpect(jsonPath("$.data.app.message").doesNotExist())

        verify(exactly = 1) {
            getAppVersionUsecase.getAppVersion(
                GetAppVersionCommand(os = AppOs.ANDROID, version = "1.1.0")
            )
        }
    }

    private companion object {
        const val STORE_URL = "https://play.google.com/store/apps/details?id=com.challa"
    }
}
