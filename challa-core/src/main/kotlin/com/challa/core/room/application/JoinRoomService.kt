package com.challa.core.room.application

import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.input.JoinRoomCommand
import com.challa.core.room.port.input.JoinRoomResult
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class JoinRoomService(private val roomRepository: RoomRepository, private val roomUserRepository: RoomUserRepository) :
    JoinRoomUsecase {
    override fun joinRoom(joinRoomCommand: JoinRoomCommand): JoinRoomResult {
        val room = roomRepository.findByInvitationCode(joinRoomCommand.invitationCode)
            ?: throw InvitationCodeNotFoundException()
        val newMember = RoomUser.createMember(
            roomId = room.id!!,
            userId = joinRoomCommand.userId
        )

        val savedRoom = roomUserRepository.save(newMember)

        return JoinRoomResult(
            id = savedRoom.id!!
        )
    }
}

class InvitationCodeNotFoundException : RuntimeException("Invitation code not found")
