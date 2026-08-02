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
}
