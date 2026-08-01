package com.challa.persistence.photo

import org.springframework.data.jpa.repository.JpaRepository

interface PhotoRepository : JpaRepository<PhotoEntity, Long>
