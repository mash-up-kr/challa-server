package com.challa.persistence.photo

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class PhotoCommandAdapter(
    private val photoRepository: PhotoRepository,
) {
}
