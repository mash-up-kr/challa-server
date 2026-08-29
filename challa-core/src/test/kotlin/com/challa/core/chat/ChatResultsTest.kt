package com.challa.core.chat

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.core.room.domain.Room
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class ChatResultsTest {
    @Test
    fun `keeps a chat and uses fallback user information when its user no longer exists`() {
        val result = GetChatsResult.from(
            room = room(),
            chats = listOf(
                Chat(
                    id = 1L,
                    type = ChatType.DEFAULT,
                    content = "안녕하세요",
                    roomId = 11L,
                    userId = 7L,
                    createdAt = LocalDateTime.of(2026, 8, 20, 12, 0)
                )
            ),
            photos = emptyMap(),
            users = emptyMap()
        )

        val chat = result.chats.single()
        assertEquals(0L, chat.user?.id)
        assertEquals("탈퇴한 사용자", chat.user?.name)
        assertNull(chat.user?.profileImageUrl)
    }

    private fun room() = Room.create(
        title = "Trip",
        totalPhotoCount = 24L,
        invitationCode = "123456",
        coverImageUrl = null,
        coverStickerId = null,
        coverStickerColorId = null
    )
}
