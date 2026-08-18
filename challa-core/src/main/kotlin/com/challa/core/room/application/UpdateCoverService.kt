package com.challa.core.room.application

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.UpdateCoverCommand
import com.challa.core.room.port.input.UpdateCoverUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class UpdateCoverService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository
) : UpdateCoverUsecase {
    override fun updateCover(updateCoverCommand: UpdateCoverCommand) {
        roomUserRepository.findByUserIdAndRoomId(
            userId = updateCoverCommand.userId,
            roomId = updateCoverCommand.roomId
        ) ?: throw NoMatchingRoomException()

        roomRepository.updateCover(
            roomId = updateCoverCommand.roomId,
            coverImageUrl = updateCoverCommand.coverImageUrl,
            coverStickerId = updateCoverCommand.coverStickerId,
            coverStickerColorId = updateCoverCommand.coverStickerColorId
        )
    }
}
