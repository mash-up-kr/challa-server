package com.challa.core.room.port.input

interface ValidateRoomUserUsecase {
    fun validateRoomMember(command: ValidateRoomUserCommand): ValidateRoomUserResult
}
