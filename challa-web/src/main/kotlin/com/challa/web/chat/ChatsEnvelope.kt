package com.challa.web.chat

data class ChatsEnvelope<T : Any>(val chats: List<T>)
