package com.challa.persistence

import com.challa.core.chat.domain.Chat
import com.challa.core.chat.domain.ChatType
import com.challa.persistence.chat.ChatAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(ChatAdapter::class)
class ChatAdapterTest {
    @Autowired
    private lateinit var adapter: ChatAdapter

    @Test
    fun `findAllByPhotoId returns only chats for the requested photo`() {
        val firstChat = adapter.save(chat(photoId = 31, content = "좋아요"))
        val secondChat = adapter.save(chat(photoId = 31, content = "멋진 사진"))
        adapter.save(chat(photoId = 32, content = "다른 사진"))

        val foundIds = adapter.findAllByPhotoId(31).map { it.id }.toSet()

        assertEquals(setOf(firstChat.id, secondChat.id), foundIds)
    }

    private fun chat(photoId: Long, content: String) = Chat(
        type = ChatType.COMMENT,
        content = content,
        photoId = photoId,
        roomId = 11,
        userId = 7
    )
}
