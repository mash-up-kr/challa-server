package com.challa.core.photo

import com.challa.core.chat.ChatRepository
import org.springframework.stereotype.Service

@Service
class GetPhotoDetailService(private val photoRepository: PhotoRepository, private val chatRepository: ChatRepository) :
    GetPhotoDetailUseCase {
    override fun getPhotoDetail(command: GetPhotoDetailCommand): GetPhotoDetailResult {
        val photo = photoRepository.findByIdAndUserId(
            photoId = command.photoId,
            userId = command.userId
        ) ?: throw PhotoNotFoundException()
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
