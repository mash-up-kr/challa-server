package com.challa.core.room.port.output

import com.challa.core.room.domain.RoomCoverSticker

interface RoomCoverStickerProvider {
    fun getAll(): List<RoomCoverSticker>
}
