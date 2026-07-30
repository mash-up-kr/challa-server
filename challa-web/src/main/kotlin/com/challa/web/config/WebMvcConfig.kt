package com.challa.web.config

import com.challa.web.security.AuthUserIdArgumentResolver
import com.challa.web.security.AuthenticationInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val authUserIdArgumentResolver: AuthUserIdArgumentResolver,
    private val authenticationInterceptor: AuthenticationInterceptor
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(authUserIdArgumentResolver)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/api/**")
    }
}
