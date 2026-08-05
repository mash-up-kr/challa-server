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
    private val getShootableRoomsUsecase: GetShootableRoomsUsecase,
    private val getRoomUsersUsecase: GetRoomUsersUsecase
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
    fun joinRoom(
        @AuthUserId userId: Long,
        @RequestBody request: RoomEnvelope<JoinRoomRequest>
    ): ApiResponse<RoomEnvelope<JoinRoomResponse>> {
        val result = joinRoomUsecase.joinRoom(request.room.toCommand(userId))

        return ApiResponse.ok(RoomEnvelope(JoinRoomResponse.fromResult(result)))
    }

    @GetMapping
    fun listRooms(
        @AuthUserId userId: Long,
        @RequestParam status: List<RoomStatus>
    ): ApiResponse<RoomEnvelope<List<ListRoomResponse>>> {
        val result = listRoomsUsecase.listRooms(ListRoomsCommand(userId = userId, status = status))

        return ApiResponse.ok(RoomEnvelope(result.roomProjections.map(ListRoomResponse::fromResult)))
    }

    @GetMapping("/{roomId}")
    fun getRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<RoomEnvelope<GetRoomResponse>> {
        val result = getRoomUsecase.getRoom(GetRoomCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(RoomEnvelope(GetRoomResponse.fromResult(result)))
    }

    @GetMapping("/shootable")
    fun getShootableRooms(@AuthUserId userId: Long): ApiResponse<RoomEnvelope<List<GetShootableRoomResponse>>> {
        val result = getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = userId))

        return ApiResponse.ok(RoomEnvelope(result.rooms.map(GetShootableRoomResponse::fromResult)))
    }

    @GetMapping("/{roomId}/users")
    fun getRoomUsers(
        @AuthUserId userId: Long,
        @PathVariable roomId: Long
    ): ApiResponse<RoomEnvelope<List<GetRoomUserResponse>>> {
        val result = getRoomUsersUsecase.getRoomUsers(GetRoomUsersCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(RoomEnvelope(result.userProjections.map(GetRoomUserResponse::fromResult)))
    }
}
