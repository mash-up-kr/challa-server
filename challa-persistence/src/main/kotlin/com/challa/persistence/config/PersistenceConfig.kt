package com.challa.persistence.config

import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@Configuration
@EntityScan(basePackages = ["com.challa.persistence"])
@EnableJpaRepositories(basePackages = ["com.challa.persistence"])
@EnableJpaAuditing
class PersistenceConfig
