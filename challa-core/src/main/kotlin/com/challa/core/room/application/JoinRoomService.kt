package com.challa.core.room.application

import com.challa.core.room.domain.RoomParticipant
import com.challa.core.room.port.input.JoinRoomCommand
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.core.room.port.output.RoomParticipantRepository
import com.challa.core.room.port.output.RoomRepository
import org.springframework.stereotype.Service

@Service
class JoinRoomService(
    private val roomRepository: RoomRepository,
    private val roomParticipantRepository: RoomParticipantRepository
) : JoinRoomUsecase {
    override fun joinRoom(joinRoomCommand: JoinRoomCommand) {
        val room = roomRepository.findByInviteCode(joinRoomCommand.inviteCode)
            ?: throw InviteCodeNotFoundException()
        val newMember = RoomParticipant.createMember(
            roomId = requireNotNull(room.roomId),
            userId = joinRoomCommand.userId
        )

        roomParticipantRepository.save(newMember)
    }
}

class InviteCodeNotFoundException : RuntimeException("Invite code not found")
