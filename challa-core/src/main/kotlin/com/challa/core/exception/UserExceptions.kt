package com.challa.core.exception

sealed class InvalidProfileException(message: String) : RuntimeException(message)

class InvalidNicknameException(message: String) : InvalidProfileException(message)

class InvalidProfileImageUrlException(message: String) : InvalidProfileException(message)

class NicknameSuggestionUnavailableException(message: String = "No usable random nickname available") :
    RuntimeException(message)
