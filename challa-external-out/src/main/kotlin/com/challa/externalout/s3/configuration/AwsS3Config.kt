package com.challa.externalout.s3.configuration

import com.challa.externalout.s3.properties.S3Properties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

@Configuration
class AwsS3Config(private val s3Properties: S3Properties) {

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            s3Properties.accessKey,
            s3Properties.secretKey
        )

        return S3Client.builder()
            .region(Region.of(s3Properties.region))
            .credentialsProvider(
                StaticCredentialsProvider.create(credentials)
            )
            .build()
    }
}
