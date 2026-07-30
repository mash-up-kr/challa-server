package com.challa.persistence.user

import com.challa.core.user.RandomNicknameGenerator
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

@Component
class RandomNicknameGeneratorAdapter(private val jpa: RandomNicknameSourceJpaRepository) : RandomNicknameGenerator {
    @Transactional(readOnly = true)
    override fun generate(): String? {
        val adjective = pickRandom(RandomNicknameSourceEntity.TYPE_ADJECTIVE) ?: return null
        val noun = pickRandom(RandomNicknameSourceEntity.TYPE_NOUN) ?: return null
        return "$adjective $noun"
    }

    private fun pickRandom(type: String): String? {
        val count = jpa.countByType(type)
        if (count == 0L) return null

        val offset = Random.nextLong(count).toInt()
        return jpa.findByType(type, PageRequest.of(offset, 1, Sort.by("id")))
            .firstOrNull()?.value
    }
}
