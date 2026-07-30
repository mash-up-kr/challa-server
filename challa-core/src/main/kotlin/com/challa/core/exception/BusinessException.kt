package com.challa.core.exception

abstract class BusinessException(message: String? = null, cause: Throwable? = null) : RuntimeException(message, cause)
