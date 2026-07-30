package com.challa.web.common.exception

import com.challa.core.exception.BusinessException
import com.challa.web.common.response.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException::class)
    @ResponseStatus(HttpStatus.OK)
    fun handleBusinessException(
        ex: BusinessException
    ): ApiResponse<Nothing> = ApiResponse.error(
        message = ex.message
    )
}
