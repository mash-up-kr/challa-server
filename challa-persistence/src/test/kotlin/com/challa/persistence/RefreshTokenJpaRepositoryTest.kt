package com.challa.persistence

import com.challa.persistence.entity.RefreshTokenEntity
import com.challa.persistence.repository.RefreshTokenJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager
import org.springframework.dao.DataIntegrityViolationException
import java.time.Instant

@DataJpaTest
class RefreshTokenJpaRepositoryTest {

    @Autowired
    private lateinit var repository: RefreshTokenJpaRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    private fun newToken(userId: Long, hash: String) = RefreshTokenEntity(
        userId = userId,
        tokenHash = hash,
        expiresAt = Instant.parse("2026-08-12T00:00:00Z")
    )

    @Test
    fun `saves and finds a token by its hash`() {
        repository.save(newToken(userId = 1, hash = "hash-1"))

        val found = repository.findByTokenHash("hash-1")

        assertNotNull(found)
        assertEquals(1L, found!!.userId)
        assertFalse(found.revoked)
    }

    @Test
    fun `token hash is unique`() {
        repository.saveAndFlush(newToken(userId = 1, hash = "dup"))

        assertThrows(DataIntegrityViolationException::class.java) {
            repository.saveAndFlush(newToken(userId = 2, hash = "dup"))
        }
    }

    @Test
    fun `revokeAllByUserId revokes only the target user's active tokens`() {
        repository.save(newToken(userId = 1, hash = "u1-a"))
        repository.save(newToken(userId = 1, hash = "u1-b"))
        repository.save(newToken(userId = 2, hash = "u2-a"))
        entityManager.flush()

        repository.revokeAllByUserId(1, Instant.parse("2026-07-12T00:00:00Z"))

        assertTrue(repository.findByTokenHash("u1-a")!!.revoked)
        assertTrue(repository.findByTokenHash("u1-b")!!.revoked)
        assertFalse(repository.findByTokenHash("u2-a")!!.revoked)
    }

    @Test
    fun `markRevoked revokes a single token`() {
        val saved = repository.saveAndFlush(newToken(userId = 5, hash = "single"))

        val affected = repository.markRevoked(saved.id!!, Instant.parse("2026-07-12T00:00:00Z"))

        assertEquals(1, affected)
        val reloaded = repository.findByTokenHash("single")!!
        assertTrue(reloaded.revoked)
        assertNotNull(reloaded.rotatedAt)
    }

    @Test
    fun `markRevoked is compare-and-set - second attempt affects no rows`() {
        val saved = repository.saveAndFlush(newToken(userId = 6, hash = "cas"))
        val now = Instant.parse("2026-07-12T00:00:00Z")

        assertEquals(1, repository.markRevoked(saved.id!!, now))
        assertEquals(0, repository.markRevoked(saved.id!!, now))
    }
}
