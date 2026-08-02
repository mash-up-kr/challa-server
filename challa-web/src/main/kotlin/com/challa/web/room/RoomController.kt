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

    // 여기서 requestParam 에 빈 리스트 들어오면 거르게 하기
    @GetMapping
    fun listRooms(@AuthUserId userId: Long, @RequestParam status: List<RoomStatus>): ApiResponse<ListRoomsResponse> {
        val result = listRoomsUsecase.listRooms(ListRoomsCommand(userId = userId, status = status))

        return ApiResponse.ok(ListRoomsResponse.fromResult(result))
    }

    @GetMapping("/{roomId}")
    fun getRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<GetRoomResponse> {
        val result = getRoomUsecase.getRoom(GetRoomCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(GetRoomResponse.fromResult(result))
    }

    @GetMapping("/shootable")
    fun getShootableRooms(@AuthUserId userId: Long): ApiResponse<GetShootableRoomsResponse> {
        val result = getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = userId))

        return ApiResponse.ok(GetShootableRoomsResponse.fromResult(result))
    }
}
