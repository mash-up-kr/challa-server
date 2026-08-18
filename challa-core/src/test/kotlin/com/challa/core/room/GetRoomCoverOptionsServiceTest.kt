package com.challa.core.room

import com.challa.core.room.application.GetRoomCoverOptionsService
import com.challa.core.room.domain.RoomCoverSticker
import com.challa.core.room.domain.RoomStickerColor
import com.challa.core.room.port.input.GetRoomCoverOptionsResult
import com.challa.core.room.port.output.RoomCoverStickerProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetRoomCoverOptionsServiceTest {
    private val roomCoverStickerProvider = mockk<RoomCoverStickerProvider>()
    private val service = GetRoomCoverOptionsService(roomCoverStickerProvider)

    @Test
    fun `returns room cover options from the provider`() {
        every { roomCoverStickerProvider.getAllStickers() } returns listOf(
            RoomCoverSticker(id = 1L, fileUrl = "https://bucket/stickers/1.png")
        )
        every { roomCoverStickerProvider.getAllColors() } returns listOf(
            RoomStickerColor(id = 2L, name = "yellow", hex = "#FFD54F")
        )

        val result = service.getRoomCoverOptions()

        assertEquals(
            GetRoomCoverOptionsResult(
                stickers = listOf(
                    GetRoomCoverOptionsResult.Sticker(
                        id = 1L,
                        imageUrl = "https://bucket/stickers/1.png"
                    )
                ),
                colors = listOf(
                    GetRoomCoverOptionsResult.Color(
                        id = 2L,
                        name = "yellow",
                        hex = "#FFD54F"
                    )
                )
            ),
            result
        )
        verify(exactly = 1) { roomCoverStickerProvider.getAllStickers() }
        verify(exactly = 1) { roomCoverStickerProvider.getAllColors() }
    }

    @Test
    fun `returns empty options when the provider is empty`() {
        every { roomCoverStickerProvider.getAllStickers() } returns emptyList()
        every { roomCoverStickerProvider.getAllColors() } returns emptyList()

        val result = service.getRoomCoverOptions()

        assertEquals(GetRoomCoverOptionsResult(stickers = emptyList(), colors = emptyList()), result)
        verify(exactly = 1) { roomCoverStickerProvider.getAllStickers() }
        verify(exactly = 1) { roomCoverStickerProvider.getAllColors() }
    }
}
