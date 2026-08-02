package com.challa.core.photo

import com.challa.core.chat.domain.Chat

data class PhotoDetail(val id: Long, val chats: List<Chat>)
