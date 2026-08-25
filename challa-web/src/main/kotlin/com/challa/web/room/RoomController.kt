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
    private val getRoomUsersUsecase: GetRoomUsersUsecase,
    private val getRoomCoverOptionsUsecase: GetRoomCoverOptionsUsecase,
    private val updateCoverUsecase: UpdateCoverUsecase,
    private val updateTitleUsecase: UpdateTitleUsecase,
    private val checkPhotoPrintCompletionUsecase: CheckPhotoPrintCompletionUsecase,
    private val deleteRoomUsecase: DeleteRoomUsecase
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
    ): ApiResponse<RoomsEnvelope<ListRoomResponse>> {
        val result = listRoomsUsecase.listRooms(ListRoomsCommand(userId = userId, status = status))

        return ApiResponse.ok(RoomsEnvelope(result.roomProjections.map(ListRoomResponse::fromResult)))
    }

    @GetMapping("/{roomId}")
    fun getRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<RoomEnvelope<GetRoomResponse>> {
        val result = getRoomUsecase.getRoom(GetRoomCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(RoomEnvelope(GetRoomResponse.fromResult(result)))
    }

    @DeleteMapping("/{roomId}")
    fun deleteRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<Unit?> {
        deleteRoomUsecase.deleteRoom(DeleteRoomCommand(userId = userId, roomId = roomId))

        return ApiResponse.empty()
    }

    @PutMapping("/{roomId}/cover")
    fun updateCover(
        @AuthUserId userId: Long,
        @PathVariable roomId: Long,
        @RequestBody request: RoomEnvelope<UpdateCoverRequest>
    ): ApiResponse<Unit?> {
        updateCoverUsecase.updateCover(request.room.toCommand(userId = userId, roomId = roomId))

        return ApiResponse.empty()
    }

    @GetMapping("/shootable")
    fun getShootableRooms(@AuthUserId userId: Long): ApiResponse<RoomsEnvelope<GetShootableRoomResponse>> {
        val result = getShootableRoomsUsecase.getShootableRooms(GetShootableRoomsCommand(userId = userId))

        return ApiResponse.ok(RoomsEnvelope(result.rooms.map(GetShootableRoomResponse::fromResult)))
    }

    @GetMapping("/cover-options")
    fun getRoomCoverOptions(): ApiResponse<RoomEnvelope<GetRoomCoverOptionsResponse>> {
        val result = getRoomCoverOptionsUsecase.getRoomCoverOptions()

        return ApiResponse.ok(RoomEnvelope(GetRoomCoverOptionsResponse.fromResult(result)))
    }

    @GetMapping("/{roomId}/users")
    fun getRoomUsers(
        @AuthUserId userId: Long,
        @PathVariable roomId: Long
    ): ApiResponse<UsersEnvelope<GetRoomUserResponse>> {
        val result = getRoomUsersUsecase.getRoomUsers(GetRoomUsersCommand(userId = userId, roomId = roomId))

        return ApiResponse.ok(UsersEnvelope(result.userProjections.map(GetRoomUserResponse::fromResult)))
    }

    @PutMapping("/{roomId}/title")
    fun updateTitle(
        @AuthUserId userId: Long,
        @PathVariable roomId: Long,
        @RequestBody request: RoomEnvelope<UpdateTitleRequest>
    ): ApiResponse<Unit?> {
        updateTitleUsecase.updateTitle(request.room.toCommand(userId = userId, roomId = roomId))

        return ApiResponse.empty()
    }

    @PutMapping("/{roomId}/photo-print-completion/check")
    fun checkRoom(@AuthUserId userId: Long, @PathVariable roomId: Long): ApiResponse<Unit?> {
        checkPhotoPrintCompletionUsecase.checkPhotoPrintCompletion(
            CheckPhotoPrintCompletionCommand(userId = userId, roomId = roomId)
        )

        return ApiResponse.empty()
    }
}
