package com.challa.core.room.application

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.UpdateTitleCommand
import com.challa.core.room.port.input.UpdateTitleUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class UpdateTitleService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository
) : UpdateTitleUsecase {
    override fun updateTitle(updateTitleCommand: UpdateTitleCommand) {
        roomUserRepository.findByUserIdAndRoomId(
            userId = updateTitleCommand.userId,
            roomId = updateTitleCommand.roomId
        ) ?: throw NoMatchingRoomException()

        roomRepository.updateTitle(
            roomId = updateTitleCommand.roomId,
            title = updateTitleCommand.title
        )
    }
}
