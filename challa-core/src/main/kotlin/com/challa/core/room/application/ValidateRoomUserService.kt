package com.challa.core.room.application

import com.challa.core.room.port.input.ValidateRoomUserCommand
import com.challa.core.room.port.input.ValidateRoomUserResult
import com.challa.core.room.port.input.ValidateRoomUserUsecase
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class ValidateRoomUserService(private val roomUserRepository: RoomUserRepository) : ValidateRoomUserUsecase {
    override fun validateRoomMember(command: ValidateRoomUserCommand): ValidateRoomUserResult {
        val roomUser = roomUserRepository.findByUserIdAndRoomId(userId = command.userId, roomId = command.roomId)
        val isValid = roomUser != null

        return ValidateRoomUserResult(
            isValid = isValid
        )
    }
}
