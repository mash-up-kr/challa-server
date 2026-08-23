package com.challa.core.room.port.input

data class UpdateTitleCommand(val userId: Long, val roomId: Long, val title: String)
