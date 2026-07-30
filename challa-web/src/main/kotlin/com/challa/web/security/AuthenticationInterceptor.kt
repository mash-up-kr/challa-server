package com.challa.web.security

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthenticationInterceptor : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        if (handler !is HandlerMethod) return true
        if (isPublic(handler)) return true
        if (request.getAttribute(JwtAuthenticationFilter.AUTH_USER_ID_ATTRIBUTE) == null) {
            throw UnauthenticatedException()
        }
        return true
    }

    companion object {
        fun isPublic(handlerMethod: HandlerMethod): Boolean =
            handlerMethod.hasMethodAnnotation(PublicEndpoint::class.java) ||
                handlerMethod.beanType.isAnnotationPresent(PublicEndpoint::class.java)
    }
}
