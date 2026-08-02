package com.challa.web.photo.dto

import com.challa.core.chat.domain.Chat
import com.challa.core.photo.GetPhotoDetailResult

data class GetPhotoDetailResponse(val id: Long, val chats: List<Chat>) {
    companion object {
        fun fromResult(result: GetPhotoDetailResult) = GetPhotoDetailResponse(
            id = result.photoDetail.id,
            chats = result.photoDetail.chats
        )
    }
}
