package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomCoverSticker
import com.challa.core.room.domain.RoomStickerColor

interface RoomCoverStickerProvider {
    fun getAllStickers(): List<RoomCoverSticker>

    fun getAllColors(): List<RoomStickerColor>

    fun findStickerById(id: Long): RoomCoverSticker? = getAllStickers().firstOrNull { it.id == id }

    fun findColorById(id: Long): RoomStickerColor? = getAllColors().firstOrNull { it.id == id }
}
