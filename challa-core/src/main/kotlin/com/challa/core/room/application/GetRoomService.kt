package com.challa.core.room.application

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.GetRoomCommand
import com.challa.core.room.port.input.GetRoomResult
import com.challa.core.room.port.input.GetRoomUsecase
import com.challa.core.room.port.output.RoomParticipantRepository
import com.challa.core.room.port.output.RoomRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GetRoomService(
    private val roomParticipantRepository: RoomParticipantRepository,
    private val roomRepository: RoomRepository
) : GetRoomUsecase {
    override fun getRoom(getRoomCommand: GetRoomCommand): GetRoomResult {
        roomParticipantRepository.findByUserIdAndRoomId(
            userId = getRoomCommand.userId,
            roomId = getRoomCommand.roomId
        ) ?: throw NoMatchingRoomException()

        val room = roomRepository.findByRoomId(getRoomCommand.roomId)
            ?: throw NoMatchingRoomException()
        val shouldCompletePrinting =
            room.roomStatus == RoomStatus.PRINT_PENDING &&
                room.printCompletionAt?.isBefore(LocalDateTime.now()) == true

        if (!shouldCompletePrinting) {
            return GetRoomResult(room = room)
        }

        roomRepository.updateRoomsStatus(
            roomIds = listOf(requireNotNull(room.roomId)),
            roomStatus = RoomStatus.PRINT_COMPLETED
        )

        return GetRoomResult(room = room.copy(roomStatus = RoomStatus.PRINT_COMPLETED))
    }
}

class NoMatchingRoomException : RuntimeException("Room not found")
