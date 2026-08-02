package com.challa.core.photo

data class Photo(
    val id: Long? = null,
    val roomId: Long,
    val userId: Long,
    val filterId: String,
    val imageUrl: String? = null
)
