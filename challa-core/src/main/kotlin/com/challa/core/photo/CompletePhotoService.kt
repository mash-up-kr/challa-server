package com.challa.core.photo

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.upload.UploadedObjectDeleter
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

private const val PHOTO_PRINT_COMPLETION_MINUTES = 3L

@Service
class CompletePhotoService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository,
    private val photoRepository: PhotoRepository,
    private val uploadedObjectDeleter: UploadedObjectDeleter
) : CompletePhotoUseCase {
    @Transactional
    override fun complete(command: CompletePhotoCommand): CompletePhotoResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        roomRepository.findByRoomId(command.roomId) ?: throw NoMatchingRoomException()

        if (!roomRepository.decrementRemainedPhotoCount(command.roomId)) {
            uploadedObjectDeleter.delete(command.imageUrl)
            throw NoRemainedPhotoException()
        }

        val remainedPhotoCount =
            roomRepository.findByRoomId(command.roomId)?.remainedPhotoCount ?: throw NoMatchingRoomException()
        // 필름 사용 완료 : RoomStatus.SHOOTING -> RoomStatus.PHOTO_PRINT_PENDING, photoPrintCompletedAt 을 3분 뒤로 설정
        if (remainedPhotoCount == 0L) {
            roomRepository.markPhotoPrintPending(
                roomId = command.roomId,
                photoPrintCompletedAt = LocalDateTime.now().plusMinutes(PHOTO_PRINT_COMPLETION_MINUTES)
            )
        }

        photoRepository.save(
            Photo(
                roomId = command.roomId,
                userId = command.userId,
                filterId = command.cameraFilterName,
                imageUrl = command.imageUrl
            )
        )

        return CompletePhotoResult(remainedPhotoCount)
    }
}

class NoRemainedPhotoException : RuntimeException("촬영 가능한 장 수가 없습니다")
