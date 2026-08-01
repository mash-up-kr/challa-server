package com.challa.core.room.application

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomParticipant
import com.challa.core.room.port.input.CreateRoomCommand
import com.challa.core.room.port.input.CreateRoomResult
import com.challa.core.room.port.input.CreateRoomUsecase
import com.challa.core.room.port.output.InviteCodeConflictException
import com.challa.core.room.port.output.RoomParticipantRepository
import com.challa.core.room.port.output.RoomRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.security.SecureRandom

const val MAX_RETRY_COUNT = 5
private const val INVITE_CODE_LENGTH = 6
private const val CHARACTERS = "0123456789"

@Service
class CreateRoomService(
    private val roomRepository: RoomRepository,
    private val roomParticipantRepository: RoomParticipantRepository,
    transactionManager: PlatformTransactionManager
) : CreateRoomUsecase {
    private val transactionTemplate = TransactionTemplate(transactionManager)
    private val secureRandom = SecureRandom()

    override fun createRoom(input: CreateRoomCommand): CreateRoomResult {
        repeat(MAX_RETRY_COUNT) {
            try {
                val result = transactionTemplate.execute {
                    createRoomWithOwner(input)
                }

                return CreateRoomResult(
                    inviteCode = result.inviteCode
                )
            } catch (e: InviteCodeConflictException) {
                logger.info("hmm.. invite code is already in use")
            }
        }

        throw InviteCodeAllocationFailedException()
    }

    private fun createRoomWithOwner(input: CreateRoomCommand): Room {
        val room = Room.create(
            title = input.roomTitle,
            filmLimit = input.filmLimit,
            inviteCode = generateInviteCode()
        )
        val savedRoom = roomRepository.save(room)

        val newMember = RoomParticipant.createOwner(
            roomId = savedRoom.roomId ?: throw IllegalArgumentException("roomId is null"),
            userId = input.userId
        )
        roomParticipantRepository.save(newMember)

        return savedRoom
    }

    private fun generateInviteCode(): String = buildString(INVITE_CODE_LENGTH) {
        repeat(INVITE_CODE_LENGTH) {
            append(
                CHARACTERS[
                    secureRandom.nextInt(CHARACTERS.length)
                ]
            )
        }
    }

    private val logger = LoggerFactory.getLogger(CreateRoomService::class.java)
}

class InviteCodeAllocationFailedException : RuntimeException("Invite code allocation failed")
