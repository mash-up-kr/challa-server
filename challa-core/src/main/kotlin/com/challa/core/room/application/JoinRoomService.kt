package com.challa.core.room.application

import com.challa.core.room.application.event.MemberJoinedEvent
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.input.JoinRoomCommand
import com.challa.core.room.port.input.JoinRoomResult
import com.challa.core.room.port.input.JoinRoomUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.user.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class JoinRoomService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository,
    private val applicationEventPublisher: ApplicationEventPublisher,
    private val userRepository: UserRepository
) : JoinRoomUsecase {
    @Transactional
    override fun joinRoom(joinRoomCommand: JoinRoomCommand): JoinRoomResult {
        val room = roomRepository.findByInvitationCode(joinRoomCommand.invitationCode)
            ?: throw InvitationCodeNotFoundException()
        val newMember = RoomUser.createMember(
            roomId = room.id!!,
            userId = joinRoomCommand.userId
        )

        val existingRoomUser = roomUserRepository.insertIfAbsent(newMember)
        // 이미 참여 중인 사용자의 재요청은 저장 및 참여 이벤트 발행 없이 기존 방 ID를 반환
        if (!existingRoomUser) {
            return JoinRoomResult(
                id = room.id
            )
        }

        applicationEventPublisher.publishEvent(
            MemberJoinedEvent(
                roomId = room.id,
                roomTitle = room.title,
                userNickname = userRepository.findById(joinRoomCommand.userId)!!.nickname ?: "Unknown"
            )
        )

        return JoinRoomResult(
            id = room.id
        )
    }
}

class InvitationCodeNotFoundException : RuntimeException("Invitation code not found")
