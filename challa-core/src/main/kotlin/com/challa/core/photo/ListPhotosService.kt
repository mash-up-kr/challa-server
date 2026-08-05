package com.challa.core.photo

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class ListPhotosService(
    private val roomUserRepository: RoomUserRepository,
    private val photoRepository: PhotoRepository
) : ListPhotosUseCase {
    override fun listPhotos(command: ListPhotosCommand): ListPhotosResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        val photoProjections = photoRepository.findAllByRoomId(command.roomId).map { photo ->
            ListPhotosResult.PhotoProjection(
                id = requireNotNull(photo.id),
                imageUrl = photo.imageUrl
            )
        }

        return ListPhotosResult(photoProjections = photoProjections)
    }
}
