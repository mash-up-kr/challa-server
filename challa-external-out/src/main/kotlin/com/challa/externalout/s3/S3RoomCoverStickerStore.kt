package com.challa.externalout.s3

import com.challa.core.room.domain.RoomCoverSticker
import com.challa.core.room.port.output.RoomCoverStickerProvider
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
class S3RoomCoverStickerStore(
    private val s3PreLoader: S3PreLoader,
    private val filePathProperties: S3FilePathProperties
) : RoomCoverStickerProvider {
    @Volatile
    private var roomCoverStickers: List<RoomCoverSticker> = emptyList()

    @EventListener(ApplicationReadyEvent::class)
    fun initialize() {
        roomCoverStickers = s3PreLoader.load(
            filePathProperties.roomCoverStickers,
            typeReference = object : TypeReference<RoomCoverStickersResponse>() {}
        ).roomCoverStickers
    }

    @Scheduled(initialDelay = 10 * 60 * 1000, fixedDelay = 10 * 60 * 1000)
    fun refresh() {
        initialize()
    }

    override fun getAll(): List<RoomCoverSticker> = roomCoverStickers
}

data class RoomCoverStickersResponse(
    @param:JsonProperty("room-cover-stickers")
    val roomCoverStickers: List<RoomCoverSticker>
)
