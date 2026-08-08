package com.challa.web.common.response

data class WebSocketResponse<T>(val type: WebSocketResponseType, val message: String? = null, val data: T? = null) {
    companion object {
        fun <T> ok(type: WebSocketResponseType, data: T) = WebSocketResponse(
            type = type,
            data = data
        )

        fun error(message: String): WebSocketResponse<Unit> = WebSocketResponse(
            type = WebSocketResponseType.ERROR,
            message = message
        )
    }
}
