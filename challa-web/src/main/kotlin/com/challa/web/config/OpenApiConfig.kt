package com.challa.web.config

import com.challa.web.security.AuthUserId
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springdoc.core.customizers.OperationCustomizer
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {
    init {
        SpringDocUtils.getConfig().addAnnotationsToIgnore(AuthUserId::class.java)
    }

    @Bean
    fun openApi(): OpenAPI = OpenAPI()
        .info(Info().title("Challa API").version("v1"))
        .components(
            Components().addSecuritySchemes(
                BEARER_SCHEME,
                SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
            )
        )

    @Bean
    fun bearerSecurityCustomizer(): OperationCustomizer = OperationCustomizer { operation, handlerMethod ->
        val requiresAuth = handlerMethod.methodParameters.any {
            it.hasParameterAnnotation(AuthUserId::class.java)
        }
        if (requiresAuth) {
            operation.addSecurityItem(SecurityRequirement().addList(BEARER_SCHEME))
        }
        operation
    }

    companion object {
        private const val BEARER_SCHEME = "bearerAuth"
    }
}
