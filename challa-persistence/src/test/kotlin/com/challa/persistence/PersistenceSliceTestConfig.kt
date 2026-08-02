package com.challa.persistence

import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootConfiguration
@EnableAutoConfiguration
@EntityScan(basePackages = ["com.challa.persistence"])
@EnableJpaRepositories(basePackages = ["com.challa.persistence"])
@EnableJpaAuditing
class PersistenceSliceTestConfig
