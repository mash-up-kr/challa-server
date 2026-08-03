package com.challa.core.room.application

import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.input.CreateRoomCommand
import com.challa.core.room.port.input.CreateRoomResult
import com.challa.core.room.port.input.CreateRoomUsecase
import com.challa.core.room.port.output.InvitationCodeConflictException
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate
import java.security.SecureRandom

private const val MAX_RETRY_COUNT = 5
private const val INVITATION_CODE_LENGTH = 6
private const val CHARACTERS = "0123456789"

@Service
class CreateRoomService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository,
    private val transactionManager: PlatformTransactionManager
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
                    id = result.id!!
                )
            } catch (e: InvitationCodeConflictException) {
                logger.info("invitation code is already in use")
            }
        }

        throw InvitationCodeAllocationFailedException()
    }

    private fun createRoomWithOwner(input: CreateRoomCommand): Room {
        val room = Room.create(
            title = input.roomTitle,
            totalPhotoCount = input.totalPhotoCount,
            invitationCode = generateInvitationCode()
        )
        val savedRoom = roomRepository.save(room)

        val newMember = RoomUser.createOwner(
            roomId = savedRoom.id ?: throw IllegalArgumentException("roomId is null"),
            userId = input.userId
        )
        roomUserRepository.save(newMember)

        return savedRoom
    }

    private fun generateInvitationCode(): String = buildString(INVITATION_CODE_LENGTH) {
        repeat(INVITATION_CODE_LENGTH) {
            append(
                CHARACTERS[
                    secureRandom.nextInt(CHARACTERS.length)
                ]
            )
        }
    }

    private val logger = LoggerFactory.getLogger(CreateRoomService::class.java)
}

class InvitationCodeAllocationFailedException : RuntimeException("Invitation code allocation failed")
