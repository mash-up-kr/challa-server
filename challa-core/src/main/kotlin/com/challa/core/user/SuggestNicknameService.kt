package com.challa.core.user

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
