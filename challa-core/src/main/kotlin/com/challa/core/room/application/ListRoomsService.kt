package com.challa.core.room.application

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsCommand
import com.challa.core.room.port.input.ListRoomsResult
import com.challa.core.room.port.input.ListRoomsUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ListRoomsService(private val roomRepository: RoomRepository, private val roomUserRepository: RoomUserRepository) :
    ListRoomsUsecase {
    override fun listRooms(listRoomsCommand: ListRoomsCommand): ListRoomsResult {
        val roomIds = roomUserRepository.findAllByUserId(listRoomsCommand.userId).map { it.roomId }
        val rooms = roomRepository.findAllByRoomIdIn(roomIds)
        val now = LocalDateTime.now()
        val newlyCompletedRoomIds = rooms
            .filter { room ->
                room.roomStatus == RoomStatus.PRINT_PENDING &&
                    room.printCompletionAt?.isBefore(now) == true
            }
            .map { requireNotNull(it.id) }
        if (newlyCompletedRoomIds.isNotEmpty()) {
            roomRepository.updateRoomsStatus(
                roomIds = newlyCompletedRoomIds,
                roomStatus = RoomStatus.PRINT_COMPLETED
            )
        }

        val newlyCompletedRoomIdSet = newlyCompletedRoomIds.toSet()
        val requestedStatusSet = listRoomsCommand.status.toSet()
        val roomProjections = rooms.map { room ->
            ListRoomsResult.RoomProjection(
                roomId = requireNotNull(room.id),
                roomStatus = if (room.id in newlyCompletedRoomIdSet) {
                    RoomStatus.PRINT_COMPLETED
                } else {
                    room.roomStatus
                }
            )
        }
            .filter { it.roomStatus in requestedStatusSet }

        return ListRoomsResult(roomProjections = roomProjections)
    }
}
