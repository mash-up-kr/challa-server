package com.challa.web.room

import com.challa.core.room.port.input.CreateRoomUsecase
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.core.room.port.input.ListRoomsCommand
import com.challa.core.room.port.input.ListRoomsUsecase
import com.challa.web.common.response.ApiResponse
import com.challa.web.room.dto.CreateRoomRequest
import com.challa.web.room.dto.CreateRoomResponse
import com.challa.web.room.dto.JoinRoomRequest
import com.challa.web.room.dto.ListRoomsResponse
import com.challa.web.security.AuthUserId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/rooms")
class RoomController(
    private val createRoomUsecase: CreateRoomUsecase,
    private val joinRoomUsecase: JoinRoomUsecase,
    private val listRoomsUsecase: ListRoomsUsecase
) {
    @PostMapping
    fun createRoom(
        @AuthUserId userId: Long,
        @RequestBody createRoomRequest: CreateRoomRequest
    ): ApiResponse<CreateRoomResponse> {
        val result = CreateRoomResponse.fromResult(
            createRoomUsecase.createRoom(createRoomRequest.toCommand(userId))
        )

        return ApiResponse.ok(result)
    }

    @PostMapping("/join")
    fun joinRoom(@AuthUserId userId: Long, @RequestBody joinRoomRequest: JoinRoomRequest): ApiResponse<Unit> {
        joinRoomUsecase.joinRoom(joinRoomRequest.toCommand(userId))

        return ApiResponse.ok(Unit)
    }

    @GetMapping
    fun listRooms(@AuthUserId userId: Long): ApiResponse<ListRoomsResponse> {
        val result = listRoomsUsecase.listRooms(ListRoomsCommand(userId = userId))

        return ApiResponse.ok(ListRoomsResponse.fromResult(result))
    }
}
