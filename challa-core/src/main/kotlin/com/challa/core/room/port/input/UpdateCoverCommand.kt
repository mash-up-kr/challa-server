package com.challa.core.room.port.input

data class UpdateCoverCommand(
    val userId: Long,
    val roomId: Long,
    val coverImageUrl: String?,
    val coverStickerId: Long?,
    val coverStickerColorId: Long?
)
