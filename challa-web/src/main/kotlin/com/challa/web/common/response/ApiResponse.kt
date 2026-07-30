package com.challa.web.common.response

data class ApiResponse<T>(val success: Boolean, val message: String, val data: T? = null) {
    companion object {
        private const val OK = "OK"

        fun <T> ok(data: T?): ApiResponse<T> = ApiResponse(success = true, message = OK, data = data)

        fun empty(): ApiResponse<Unit?> = ApiResponse(success = true, message = OK, data = null)

        fun error(message: String): ApiResponse<Unit?> = ApiResponse(success = false, message = message, data = null)
    }
}
