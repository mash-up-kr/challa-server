package com.challa.persistence.photo

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(readOnly = true)
class PhotoReadAdapter(private val photoRepository: PhotoRepository)
