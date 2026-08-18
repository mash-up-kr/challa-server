package com.challa.core.room.application

import com.challa.core.room.domain.RoomCover
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.GetRoomCommand
import com.challa.core.room.port.input.GetRoomResult
import com.challa.core.room.port.input.GetRoomUsecase
import com.challa.core.room.port.output.RoomCoverStickerProvider
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class GetRoomService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository,
    private val roomCoverStickerProvider: RoomCoverStickerProvider
) : GetRoomUsecase {
    override fun getRoom(getRoomCommand: GetRoomCommand): GetRoomResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = getRoomCommand.userId,
            roomId = getRoomCommand.roomId
        ) ?: throw NoMatchingRoomException()

        val room = roomRepository.findByRoomId(getRoomCommand.roomId)
            ?: throw NoMatchingRoomException()
        val shouldCompletePrinting =
            room.roomStatus == RoomStatus.PHOTO_PRINT_PENDING &&
                room.photoPrintCompletedAt?.isBefore(LocalDateTime.now()) == true
        val coverSticker = room.coverStickerId?.let {
            val sticker = roomCoverStickerProvider.findStickerById(it)!!
            val color = roomCoverStickerProvider.findColorById(room.coverStickerColorId!!)!!

            RoomCover.Sticker(
                id = sticker.id,
                imageUrl = sticker.fileUrl,
                color = RoomCover.Sticker.Color(
                    id = color.id,
                    name = color.name,
                    hex = color.hex
                )
            )
        }
        val getRoomResult = GetRoomResult(
            roomId = room.id!!,
            title = room.title,
            totalPhotoCount = room.totalPhotoCount,
            remainedPhotoCount = room.remainedPhotoCount,
            invitationCode = room.invitationCode,
            roomStatus = room.roomStatus,
            cover = RoomCover(
                coverImageUrl = room.coverImageUrl,
                sticker = coverSticker
            ),
            photoPrintCompletedAt = room.photoPrintCompletedAt,
            createdAt = room.createdAt,
            expiresAt = room.expiresAt
        )

        if (!shouldCompletePrinting) {
            return getRoomResult
        }

        roomRepository.updateRoomsStatus(
            roomIds = listOf(room.id),
            roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED
        )

        return getRoomResult.copy(roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED)
    }
}
