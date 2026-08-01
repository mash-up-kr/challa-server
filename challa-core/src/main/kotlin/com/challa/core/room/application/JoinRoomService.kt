package com.challa.core.room.application

import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.input.JoinRoomCommand
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class JoinRoomService(private val roomRepository: RoomRepository, private val roomUserRepository: RoomUserRepository) :
    JoinRoomUsecase {
    override fun joinRoom(joinRoomCommand: JoinRoomCommand) {
        val room = roomRepository.findByInvitationCode(joinRoomCommand.invitationCode)
            ?: throw InvitationCodeNotFoundException()
        val newMember = RoomUser.createMember(
            roomId = room.id!!,
            userId = joinRoomCommand.userId
        )

        roomUserRepository.save(newMember)
    }
}

class InvitationCodeNotFoundException : RuntimeException("Invitation code not found")
