package com.challa.core.room.application

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.CheckPhotoPrintCompletionCommand
import com.challa.core.room.port.input.CheckPhotoPrintCompletionUsecase
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class CheckPhotoPrintCompletionService(private val roomUserRepository: RoomUserRepository) :
    CheckPhotoPrintCompletionUsecase {
    override fun checkPhotoPrintCompletion(command: CheckPhotoPrintCompletionCommand) {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        roomUserRepository.markPhotoPrintCompletionChecked(
            userId = command.userId,
            roomId = command.roomId,
            checkedAt = LocalDateTime.now()
        )
    }
}
