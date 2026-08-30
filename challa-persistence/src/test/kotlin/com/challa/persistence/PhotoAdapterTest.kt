package com.challa.persistence

import com.challa.core.photo.Photo
import com.challa.persistence.photo.PhotoAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import
import org.springframework.data.domain.PageRequest

@DataJpaTest
@Import(PhotoAdapter::class)
class PhotoAdapterTest {
    @Autowired
    private lateinit var adapter: PhotoAdapter

    @Test
    fun `save persists a completed photo with its filter and image URL`() {
        val saved = adapter.save(
            Photo(
                roomId = 11,
                userId = 7,
                filterId = "filter-original",
                imageUrl = "https://bucket/photo/7/image-id"
            )
        )

        val found = adapter.findById(requireNotNull(saved.id))
        assertEquals("https://bucket/photo/7/image-id", found?.imageUrl)
        assertEquals("filter-original", found?.filterId)
    }

    @Test
    fun `findAllByRoomId returns only photos from the requested room`() {
        val firstPhoto = adapter.save(photo(roomId = 11, userId = 7, filterId = "filter-one"))
        val secondPhoto = adapter.save(photo(roomId = 11, userId = 8, filterId = "filter-two"))
        adapter.save(photo(roomId = 12, userId = 7, filterId = "filter-three"))

        val found = adapter.findSliceByRoomId(11, PageRequest.of(0, 10))
        val foundIds = found.photos.map { it.id }.toSet()

        assertEquals(setOf(firstPhoto.id, secondPhoto.id), foundIds)
        assertEquals(false, found.hasNext)
    }

    @Test
    fun `findLatestFourByRoomIdsIn returns at most four newest photos per room`() {
        val roomElevenPhotos = (1L..5L).map { sequence ->
            adapter.save(photo(roomId = 11, userId = sequence, filterId = "filter-$sequence"))
        }
        val roomTwelvePhoto = adapter.save(photo(roomId = 12, userId = 1, filterId = "filter-room-twelve"))
        adapter.save(photo(roomId = 13, userId = 1, filterId = "filter-room-thirteen"))

        val found = adapter.findLatestFourByRoomIdsIn(listOf(11, 12))

        val expectedRoomElevenPhotoIds = roomElevenPhotos.takeLast(4).map { it.id }.reversed()
        val foundRoomElevenPhotoIds = found.filter { it.roomId == 11L }.map { it.id }
        assertEquals(expectedRoomElevenPhotoIds, foundRoomElevenPhotoIds)
        assertEquals(listOf(roomTwelvePhoto.id), found.filter { it.roomId == 12L }.map { it.id })
        assertEquals(setOf(11L, 12L), found.map { it.roomId }.toSet())
    }

    private fun photo(roomId: Long, userId: Long, filterId: String) = Photo(
        roomId = roomId,
        userId = userId,
        filterId = filterId,
        imageUrl = "https://bucket/photo/$roomId/$userId/$filterId"
    )
}
