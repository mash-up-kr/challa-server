package com.challa.web.room

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.*
import com.challa.web.common.response.ApiResponse
import com.challa.web.room.dto.*
import com.challa.web.security.AuthUserId
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/rooms")
class RoomController(
    private val createRoomUsecase: CreateRoomUsecase,
    private val joinRoomUsecase: JoinRoomUsecase,
    private val listRoomsUsecase: ListRoomsUsecase,
    private val getRoomUsecase: GetRoomUsecase,
    private val getShootableRoomsUsecase: GetShootableRoomsUsecase
) {
    @PostMapping
    fun createRoom(
        @AuthUserId userId: Long,
        @RequestBody request: RoomEnvelope<CreateRoomRequest>
    ): ApiResponse<RoomEnvelope<CreateRoomResponse>> {
        val result = createRoomUsecase.createRoom(request.room.toCommand(userId))

        return ApiResponse.ok(RoomEnvelope(CreateRoomResponse.fromResult(result)))
    }

    @PostMapping("/join")
    fun joinRoom(@AuthUserId userId: Long, @RequestBody request: RoomEnvelope<JoinRoomRequest>): ApiResponse<Unit?> {
        joinRoomUsecase.joinRoom(request.room.toCommand(userId))

        return ApiResponse.empty()
    }

    @GetMapping
    fun listRooms(
        @AuthUserId userId: Long,
        @RequestParam status: List<RoomStatus>
    ): ApiResponse<RoomEnvelope<List<ListRoomsResponse>>> {
        val result = listRoomsUsecase.listRooms(ListRoomsCommand(userId = userId, status = status))

        return ApiResponse.ok(RoomEnvelope(result.roomProjections.map(ListRoomsResponse::fromResult)))
    }

    @GetMapping("/{roomId}")
    fun getRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<RoomEnvelope<GetRoomResponse>> {
        val result = getRoomUsecase.getRoom(GetRoomCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(RoomEnvelope(GetRoomResponse.fromResult(result)))
    }

    @GetMapping("/shootable")
    fun getShootableRooms(@AuthUserId userId: Long): ApiResponse<RoomEnvelope<List<GetShootableRoomsResponse>>> {
        val result = getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = userId))

        return ApiResponse.ok(RoomEnvelope(result.rooms.map(GetShootableRoomsResponse::fromResult)))
    }
}
