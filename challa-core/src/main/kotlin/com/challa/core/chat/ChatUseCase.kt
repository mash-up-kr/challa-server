package com.challa.core.chat

interface ChatUseCase {
    fun getChatsByRoomId(roomId: Long, page: Int, size: Int): GetChatsResult
    fun chat(input: CreateChatCommand): ChatResult
    fun reactForPhoto(input: CreateChatCommand): ChatResult
    fun removeChat(userId: Long, chatId: Long): Long
}
