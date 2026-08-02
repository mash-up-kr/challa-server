package com.challa.core.photo

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CompletePhotoService(private val photoRepository: PhotoRepository) : CompletePhotoUseCase {
    @Transactional
    override fun complete(command: CompletePhotoCommand): CompletePhotoResult {
        val photo = photoRepository.updateImageUrl(
            photoId = command.photoId,
            userId = command.userId,
            imageUrl = command.imageUrl
        ) ?: throw PhotoNotFoundException()

        return CompletePhotoResult(photo)
    }
}

class PhotoNotFoundException : RuntimeException("Photo not found")
