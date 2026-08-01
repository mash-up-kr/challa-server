package com.challa.core.room.port.input

data class JoinRoomCommand(val userId: Long, val inviteCode: String)
