package com.challa.web.common.response

data class ApiResponse<T>(val success: Boolean, val message: String, val data: T? = null) {
    companion object {
        fun <T> ok(data: T?, message: String = "OK"): ApiResponse<T> =
            ApiResponse(success = true, message = message, data = data)

        fun empty(message: String = "OK"): ApiResponse<Unit?> =
            ApiResponse(success = true, message = message, data = null)

        fun error(message: String): ApiResponse<Unit?> = ApiResponse(success = false, message = message, data = null)
    }
}
