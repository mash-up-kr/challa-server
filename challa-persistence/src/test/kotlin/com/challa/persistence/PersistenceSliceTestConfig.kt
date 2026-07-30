package com.challa.persistence

import com.challa.persistence.entity.UserEntity
import com.challa.persistence.repository.UserJpaRepository
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackageClasses = [UserEntity::class])
@EnableJpaRepositories(basePackageClasses = [UserJpaRepository::class])
class PersistenceSliceTestConfig
