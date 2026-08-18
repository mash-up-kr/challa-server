package com.challa.web.room.dto

import com.challa.core.room.port.input.GetRoomCoverOptionsResult

data class GetRoomCoverOptionsResponse(val stickers: List<Sticker>, val colors: List<Color>) {
    data class Sticker(val id: Long, val imageUrl: String)

    data class Color(val id: Long, val name: String, val hex: String)

    companion object {
        fun fromResult(result: GetRoomCoverOptionsResult) = GetRoomCoverOptionsResponse(
            stickers = result.stickers.map { sticker ->
                Sticker(
                    id = sticker.id,
                    imageUrl = sticker.imageUrl
                )
            },
            colors = result.colors.map { color ->
                Color(
                    id = color.id,
                    name = color.name,
                    hex = color.hex
                )
            }
        )
    }
}
