package com.challa.web.room.dto

data class RoomsEnvelope<T : Any>(val rooms: List<T>)
