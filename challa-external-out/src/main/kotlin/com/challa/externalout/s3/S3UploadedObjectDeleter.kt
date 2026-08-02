package com.challa.externalout.s3

import com.challa.core.upload.UploadedObjectDeleter
import com.challa.externalout.s3.properties.S3Properties
import org.springframework.stereotype.Component
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest
import java.net.URI

@Component
class S3UploadedObjectDeleter(private val s3Client: S3Client, private val properties: S3Properties) :
    UploadedObjectDeleter {
    override fun delete(imageUrl: String) {
        val objectKey = URI.create(imageUrl).path.removePrefix("/")
        val request = DeleteObjectRequest.builder()
            .bucket(properties.bucket)
            .key(objectKey)
            .build()

        s3Client.deleteObject(request)
    }
}
