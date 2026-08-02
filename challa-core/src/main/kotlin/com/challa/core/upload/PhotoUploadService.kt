package com.challa.core.upload

import com.challa.core.photo.Photo
import com.challa.core.photo.PhotoRepository
import com.challa.core.room.application.NoMatchingRoomException
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class PhotoUploadService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository,
    private val presignedUploadUrlIssuer: PresignedUploadUrlIssuer,
    private val photoRepository: PhotoRepository
) : IssuePhotoUploadUseCase {

    @Transactional
    override fun issue(command: IssuePhotoUploadCommand): IssuePhotoUploadResult {
        roomUserRepository.findByUserIdAndRoomId(userId = command.userId, roomId = command.roomId)
            ?: throw NoMatchingRoomException()

        val room = roomRepository.findByRoomId(command.roomId)
            ?: throw NoMatchingRoomException()
        if (room.remainedPhotoCount == 0L) {
            throw NoRemainedPhotoException()
        }

        if (!roomRepository.decrementRemainedPhotoCount(command.roomId)) {
            throw NoRemainedPhotoException()
        }

        val updatedRemainedPhotoCount =
            roomRepository.findByRoomId(command.roomId)?.remainedPhotoCount ?: throw NoMatchingRoomException()
        if (updatedRemainedPhotoCount == 0L) {
            roomRepository.updateRoomStatus(
                roomId = command.roomId,
                roomStatus = RoomStatus.PHOTO_PRINT_PENDING
            )
        }

        val photo = photoRepository.save(
            Photo(
                roomId = command.roomId,
                userId = command.userId,
                filterId = command.cameraFilterId
            )
        )
        val uploadUrl = presignedUploadUrlIssuer.issue(
            key = "photo/${command.userId}/${UUID.randomUUID()}",
            contentType = command.contentType
        )

        return IssuePhotoUploadResult(
            photoId = requireNotNull(photo.id),
            uploadUrl = uploadUrl.uploadUrl,
            imageUrl = uploadUrl.imageUrl,
            expiresInSeconds = uploadUrl.expiresInSeconds,
            remainedPhotoCount = updatedRemainedPhotoCount
        )
    }
}

class NoRemainedPhotoException : RuntimeException("촬영 가능한 장 수가 없습니다")
