package com.challa.core.room.application

import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.GetRoomCommand
import com.challa.core.room.port.input.GetRoomResult
import com.challa.core.room.port.input.GetRoomUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GetRoomService(private val roomUserRepository: RoomUserRepository, private val roomRepository: RoomRepository) :
    GetRoomUsecase {
    override fun getRoom(getRoomCommand: GetRoomCommand): GetRoomResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = getRoomCommand.userId,
            roomId = getRoomCommand.roomId
        ) ?: throw NoMatchingRoomException()

        val room = roomRepository.findByRoomId(getRoomCommand.roomId)
            ?: throw NoMatchingRoomException()
        val shouldCompletePrinting =
            room.roomStatus == RoomStatus.PHOTO_PRINT_PENDING &&
                room.photoPrintCompletedAt?.isBefore(LocalDateTime.now()) == true

        if (!shouldCompletePrinting) {
            return GetRoomResult(room = room)
        }

        roomRepository.updateRoomsStatus(
            roomIds = listOf(room.id!!),
            roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED
        )

        return GetRoomResult(room = room.copy(roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED))
    }
}
