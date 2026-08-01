package com.challa.externalout.s3.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "s3.file-path")
data class S3FilePathProperties(val cameraFilters: String)
