package com.challa.externalout.s3

import com.challa.externalout.s3.properties.S3Properties
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client

@Component
class S3PreLoader(
    private val s3Client: S3Client,
    private val s3Properties: S3Properties,
    private val objectMapper: ObjectMapper,
) {

    fun <T> load(
        filePath: String,
        typeReference: TypeReference<T>,
    ): T =
        s3Client.getObject {
            it.bucket(s3Properties.bucket)
                .key(filePath)
        }.use { input ->
            objectMapper.readValue(input, typeReference)
        }
}
