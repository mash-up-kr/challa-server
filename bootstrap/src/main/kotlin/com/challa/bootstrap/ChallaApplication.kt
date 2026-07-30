package com.challa.bootstrap

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.challa"])
@ConfigurationPropertiesScan(basePackages = ["com.challa"])
class ChallaApplication

fun main(args: Array<String>) {
    runApplication<ChallaApplication>(*args)
}
