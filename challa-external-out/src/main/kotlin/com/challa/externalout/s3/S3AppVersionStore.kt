package com.challa.externalout.s3

import com.challa.core.app.domain.AppVersion
import com.challa.core.app.port.output.AppVersionProvider
import com.challa.externalout.s3.properties.S3FilePathProperties
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class S3AppVersionStore(private val s3PreLoader: S3PreLoader, private val filePathProperties: S3FilePathProperties) :
    AppVersionProvider {
    @Volatile
    private lateinit var appVersion: AppVersion

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        appVersion = s3PreLoader.load(
            filePathProperties.appVersion,
            typeReference = object : TypeReference<AppVersion>() {}
        )
    }

    @Scheduled(initialDelay = 10 * 60 * 1000, fixedDelay = 10 * 60 * 1000)
    fun refresh() {
        initialize()
    }

    override fun getAppVersion(): AppVersion = appVersion
}
