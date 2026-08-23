package com.challa.web.room.dto

import com.challa.core.room.port.input.UpdateTitleCommand

data class UpdateTitleRequest(val title: String) {
    fun toCommand(userId: Long, roomId: Long) = UpdateTitleCommand(
        userId = userId,
        roomId = roomId,
        title = title
    )
}
