package com.challa.web.chat

import com.challa.core.chat.ChatUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
import io.swagger.v3.oas.annotations.Operation
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/chats")
class ChatController(private val chatUseCase: ChatUseCase) {

    @GetMapping("/{roomId}")
    fun getChats(
        @PathVariable roomId: Long,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ApiResponse<GetChatsResponse> {
        val result = chatUseCase.getChatsByRoomId(roomId, page, size)
        return ApiResponse.ok(GetChatsResponse.from(result))
    }

    @Operation(
        summary = "채팅",
        description = "type = DEFAULT"
    )
    @PostMapping
    fun createChat(@AuthUserId userId: Long, @RequestBody request: CreateChatRequest): ApiResponse<CreateChatResponse> {
        val result = chatUseCase.chat(request.toCommand(userId))
        return ApiResponse.ok(CreateChatResponse.from(result))
    }

    @Operation(
        summary = "사진 반응 남기기",
        description = "type = EMOJI or COMMENT"
    )
    @PostMapping("/reaction")
    fun createChatForReaction(
        @AuthUserId userId: Long,
        @RequestBody request: CreateChatRequest
    ): ApiResponse<CreateChatResponse> {
        val result = chatUseCase.reactForPhoto(request.toCommand(userId))
        return ApiResponse.ok(CreateChatResponse.from(result))
    }
}
