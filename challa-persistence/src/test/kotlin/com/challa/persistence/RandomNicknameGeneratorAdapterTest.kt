package com.challa.persistence

import com.challa.persistence.adapter.RandomNicknameGeneratorAdapter
import com.challa.persistence.entity.RandomNicknameSourceEntity
import com.challa.persistence.repository.RandomNicknameSourceJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest

@DataJpaTest
class RandomNicknameGeneratorAdapterTest {
    @Autowired
    private lateinit var repository: RandomNicknameSourceJpaRepository

    private val adapter by lazy { RandomNicknameGeneratorAdapter(repository) }

    @Test
    fun `returns null when the source table is empty`() {
        assertNull(adapter.generate())
    }

    @Test
    fun `returns null when only one of the two word types is seeded`() {
        seed(RandomNicknameSourceEntity.TYPE_ADJECTIVE, "용감한")

        assertNull(adapter.generate())
    }

    @Test
    fun `combines one adjective and one noun`() {
        seed(RandomNicknameSourceEntity.TYPE_ADJECTIVE, "용감한")
        seed(RandomNicknameSourceEntity.TYPE_NOUN, "호랑이")

        assertEquals("용감한 호랑이", adapter.generate())
    }

    @Test
    fun `only ever draws seeded words`() {
        val adjectives = listOf("용감한", "빠른", "귀여운")
        val nouns = listOf("호랑이", "고양이")
        adjectives.forEach { seed(RandomNicknameSourceEntity.TYPE_ADJECTIVE, it) }
        nouns.forEach { seed(RandomNicknameSourceEntity.TYPE_NOUN, it) }

        val drawn = (1..100).map { requireNotNull(adapter.generate()) }

        drawn.forEach { nickname ->
            val (adjective, noun) = nickname.split(" ")
            assertTrue(adjective in adjectives, "unexpected adjective in $nickname")
            assertTrue(noun in nouns, "unexpected noun in $nickname")
        }

        assertTrue(drawn.distinct().size > 1, "generator returned a constant value")
    }

    @Test
    fun `ignores rows of the other type`() {
        seed(RandomNicknameSourceEntity.TYPE_ADJECTIVE, "용감한")
        seed(RandomNicknameSourceEntity.TYPE_NOUN, "호랑이")
        seed("z", "쓰이지않는")

        repeat(20) { assertEquals("용감한 호랑이", adapter.generate()) }
    }

    private fun seed(type: String, value: String) {
        repository.save(RandomNicknameSourceEntity(type = type, value = value))
    }
}
