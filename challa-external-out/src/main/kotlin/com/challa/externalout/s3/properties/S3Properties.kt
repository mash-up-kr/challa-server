package com.challa.externalout.s3.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = "s3")
data class S3Properties(
    val bucket: String,
    val region: String,
    val presignedUrlTtl: Duration = Duration.ofMinutes(5),
    /** 비워두면 DefaultCredentialsProvider(EC2 IAM 역할, 환경변수 등)로 폴백한다. */
    val accessKey: String = "",
    val secretKey: String = ""
)
