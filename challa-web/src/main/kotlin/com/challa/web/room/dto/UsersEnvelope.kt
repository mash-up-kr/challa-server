package com.challa.web.room.dto

data class UsersEnvelope<T : Any>(val users: List<T>)
