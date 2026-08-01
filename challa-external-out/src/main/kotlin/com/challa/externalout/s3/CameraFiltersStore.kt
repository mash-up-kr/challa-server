package com.challa.externalout.s3

import com.challa.core.shoot.CameraFiltersStorage
import com.challa.core.shoot.domain.CameraFilter
import com.challa.externalout.s3.properties.S3FilePathProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Profile("!test")
@Component
class CameraFiltersStore(private val preLoader: S3PreLoader, private val filePathProperties: S3FilePathProperties) :
    CameraFiltersStorage {

    private var cameraFilters: List<CameraFilter> = emptyList()

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        cameraFilters = preLoader.load(
            filePathProperties.cameraFilters,
            object : TypeReference<CameraFiltersResponse>() {}
        ).cameraFilters
    }

    @Scheduled(initialDelay = 0, fixedDelay = 10 * 60 * 1000)
    fun refresh() {
        initialize()
    }

    override fun getAll(): List<CameraFilter> = cameraFilters
}

data class CameraFiltersResponse(
    @param:JsonProperty("camera-filters")
    val cameraFilters: List<CameraFilter>
)
