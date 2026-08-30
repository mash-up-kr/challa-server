package com.challa.core.photo

import com.challa.core.chat.ChatRepository
import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class GetPhotoDetailService(
    private val photoRepository: PhotoRepository,
    private val chatRepository: ChatRepository,
    private val roomUserRepository: RoomUserRepository
) : GetPhotoDetailUseCase {
    override fun getPhotoDetail(command: GetPhotoDetailCommand): GetPhotoDetailResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        val photo = photoRepository.findById(photoId = command.photoId)
            ?.takeIf { it.roomId == command.roomId } ?: throw PhotoNotFoundException()
        val photoId = photo.id!!
        val chats = chatRepository.findAllByPhotoId(photoId)

        return GetPhotoDetailResult(
            photoDetail = PhotoDetail(
                id = photoId,
                chats = chats
            )
        )
    }
}

class PhotoNotFoundException : RuntimeException("Photo not found")
