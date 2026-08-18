package com.challa.web.room.dto

import com.challa.core.room.port.input.UpdateCoverCommand

data class UpdateCoverRequest(val coverImageUrl: String?, val coverStickerId: Long?, val coverStickerColorId: Long?) {
    fun toCommand(userId: Long, roomId: Long) = UpdateCoverCommand(
        userId = userId,
        roomId = roomId,
        coverImageUrl = coverImageUrl,
        coverStickerId = coverStickerId,
        coverStickerColorId = coverStickerColorId
    )
}
