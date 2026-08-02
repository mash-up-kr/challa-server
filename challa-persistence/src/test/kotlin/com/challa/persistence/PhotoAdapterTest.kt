package com.challa.persistence

import com.challa.core.photo.Photo
import com.challa.persistence.photo.PhotoAdapter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(PhotoAdapter::class)
class PhotoAdapterTest {
    @Autowired
    private lateinit var adapter: PhotoAdapter

    @Test
    fun `save persists a photo placeholder with its filter`() {
        val saved = adapter.save(
            Photo(
                roomId = 11,
                userId = 7,
                filterId = "filter-original"
            )
        )

        val found = adapter.findById(requireNotNull(saved.id))
        assertNull(found?.imageUrl)
        assertEquals("filter-original", found?.filterId)
    }

    @Test
    fun `updateImageUrl updates an owned photo through dirty checking`() {
        val saved = adapter.save(
            Photo(
                roomId = 11,
                userId = 7,
                filterId = "filter-original"
            )
        )

        val updated = adapter.updateImageUrl(
            photoId = requireNotNull(saved.id),
            userId = 7,
            imageUrl = "https://bucket/photo"
        )

        assertEquals("https://bucket/photo", updated?.imageUrl)
        assertEquals("https://bucket/photo", adapter.findById(requireNotNull(saved.id))?.imageUrl)
    }

    @Test
    fun `updateImageUrl does not update another user's photo`() {
        val saved = adapter.save(
            Photo(
                roomId = 11,
                userId = 7,
                filterId = "filter-original"
            )
        )

        val updated = adapter.updateImageUrl(
            photoId = requireNotNull(saved.id),
            userId = 8,
            imageUrl = "https://bucket/photo"
        )

        assertNull(updated)
        assertNull(adapter.findById(requireNotNull(saved.id))?.imageUrl)
    }
}
