package com.challa.web.chat

import com.challa.core.chat.ChatResult
import com.challa.core.chat.ChatUseCase
import com.challa.core.chat.GetChatsResult
import com.challa.core.chat.RoomResultForChat
import com.challa.core.chat.domain.ChatType
import com.challa.web.common.exception.GlobalExceptionHandler
import com.challa.web.security.AuthenticationInterceptor
import com.challa.web.security.JwtAuthenticationFilter
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
import java.time.LocalDateTime

class ChatControllerTest {
    private val chatUseCase = mockk<ChatUseCase>()
    private val mockMvc: MockMvc = MockMvcBuilders
        .standaloneSetup(ChatController(chatUseCase))
        .addInterceptors(AuthenticationInterceptor())
        .setControllerAdvice(GlobalExceptionHandler())
        .setMessageConverters(JacksonJsonHttpMessageConverter())
        .build()

    @Test
    fun `GET chats returns chats envelope`() {
        every { chatUseCase.getChatsByRoomId(ROOM_ID, 0, 10) } returns GetChatsResult(
            room = RoomResultForChat(title = "Trip"),
            chats = listOf(
                ChatResult(
                    type = ChatType.DEFAULT,
                    content = "안녕하세요",
                    createdAt = LocalDateTime.of(2026, 8, 7, 12, 0)
                )
            )
        )

        mockMvc.perform(
            get("/api/v1/chats/$ROOM_ID")
                .param("page", "0")
                .param("size", "10")
                .requestAttr(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE, USER_ID)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.chats[0].type").value("DEFAULT"))
            .andExpect(jsonPath("$.data.chats[0].content").value("안녕하세요"))
            .andExpect(jsonPath("$.data.chat").doesNotExist())

        verify { chatUseCase.getChatsByRoomId(ROOM_ID, 0, 10) }
    }

    private companion object {
        const val USER_ID = 7L
        const val ROOM_ID = 11L
    }
}
