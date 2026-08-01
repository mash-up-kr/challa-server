package com.challa.core.room.application

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsCommand
import com.challa.core.room.port.input.ListRoomsResult
import com.challa.core.room.port.input.ListRoomsUsecase
import com.challa.core.room.port.output.RoomParticipantRepository
import com.challa.core.room.port.output.RoomRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ListRoomsService(
    private val roomRepository: RoomRepository,
    private val roomParticipantRepository: RoomParticipantRepository
) : ListRoomsUsecase {
    override fun listRooms(listRoomsCommand: ListRoomsCommand): ListRoomsResult {
        val roomIds = roomParticipantRepository.findAllByUserId(listRoomsCommand.userId).map { it.roomId }
        val rooms = roomRepository.findAllByRoomIdIn(roomIds)
        val now = LocalDateTime.now()
        val newlyCompletedRoomIds = rooms
            .filter { room ->
                room.roomStatus == RoomStatus.PRINT_PENDING &&
                    room.printCompletionAt?.isBefore(now) == true
            }
            .map { requireNotNull(it.roomId) }

        if (newlyCompletedRoomIds.isNotEmpty()) {
            roomRepository.updateRoomsStatus(
                roomIds = newlyCompletedRoomIds,
                roomStatus = RoomStatus.PRINT_COMPLETED
            )
        }

        val newlyCompletedRoomIdSet = newlyCompletedRoomIds.toSet()
        val roomProjections = rooms.map { room ->
            ListRoomsResult.RoomProjection(
                roomId = requireNotNull(room.roomId),
                roomStatus = if (room.roomId in newlyCompletedRoomIdSet) {
                    RoomStatus.PRINT_COMPLETED
                } else {
                    room.roomStatus
                }
            )
        }

        return ListRoomsResult(roomProjections = roomProjections)
    }
}
