package com.challa.core.room.application

import com.challa.core.room.port.input.GetRoomCoverOptionsResult
import com.challa.core.room.port.input.GetRoomCoverOptionsUsecase
import com.challa.core.room.port.output.RoomCoverStickerProvider
import org.springframework.stereotype.Service

@Service
class GetRoomCoverOptionsService(private val roomCoverStickerProvider: RoomCoverStickerProvider) :
    GetRoomCoverOptionsUsecase {
    override fun getRoomCoverOptions(): GetRoomCoverOptionsResult = GetRoomCoverOptionsResult(
        stickers = roomCoverStickerProvider.getAllStickers().map { sticker ->
            GetRoomCoverOptionsResult.Sticker(
                id = sticker.id,
                imageUrl = sticker.fileUrl
            )
        },
        colors = roomCoverStickerProvider.getAllColors().map { color ->
            GetRoomCoverOptionsResult.Color(
                id = color.id,
                name = color.name,
                hex = color.hex
            )
        }
    )
}
