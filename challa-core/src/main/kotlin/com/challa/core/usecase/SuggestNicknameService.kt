package com.challa.core.usecase

import com.challa.core.domain.NicknamePolicy
import com.challa.core.exception.InvalidNicknameException
import com.challa.core.exception.NicknameSuggestionUnavailableException
import com.challa.core.port.inbound.SuggestNicknameUseCase
import com.challa.core.port.outbound.RandomNicknameGenerator

class SuggestNicknameService(private val randomNicknameGenerator: RandomNicknameGenerator) : SuggestNicknameUseCase {
    override fun suggest(): String {
        val generated = randomNicknameGenerator.generate()
            ?: throw NicknameSuggestionUnavailableException()

        return try {
            NicknamePolicy.normalize(generated)
        } catch (ex: InvalidNicknameException) {
            throw NicknameSuggestionUnavailableException(
                "Random nickname source produced an invalid nickname: ${ex.message}"
            )
        }
    }
}
