package com.challa.core.room.domain

data class RoomCover(val coverImageUrl: String?, val sticker: Sticker?) {
    data class Sticker(val id: Long, val imageUrl: String, val color: Color) {
        data class Color(val id: Long, val name: String, val hex: String)
    }
}
