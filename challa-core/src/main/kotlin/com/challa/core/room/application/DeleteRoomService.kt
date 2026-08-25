package com.challa.core.room.application

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.DeleteRoomCommand
import com.challa.core.room.port.input.DeleteRoomUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class DeleteRoomService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository
) : DeleteRoomUsecase {
    @Transactional
    override fun deleteRoom(command: DeleteRoomCommand) {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        val deleted = roomRepository.softDelete(
            roomId = command.roomId,
            deletedAt = LocalDateTime.now()
        )
        if (!deleted) {
            throw NoMatchingRoomException()
        }

        roomUserRepository.deleteAllByRoomId(command.roomId)
    }
}
