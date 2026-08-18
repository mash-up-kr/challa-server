package com.challa.core.room.port.input

data class GetRoomCoverOptionsResult(val stickers: List<Sticker>, val colors: List<Color>) {
    data class Sticker(val id: Long, val imageUrl: String)

    data class Color(val id: Long, val name: String, val hex: String)
}
