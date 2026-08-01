package com.challa.externalout.s3

import com.challa.core.upload.PresignedUploadUrlIssuer
import com.challa.core.upload.UploadUrl
import org.springframework.stereotype.Component
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Utilities
import software.amazon.awssdk.services.s3.presigner.S3Presigner

@Component
class S3PresignedUrlAdapter(private val properties: S3Properties) : PresignedUploadUrlIssuer {
    private val region = Region.of(properties.region)

    // AWS SDK 는 System.getenv 만 보고 Spring Environment 를 보지 않는다.
    // Infisical 시크릿은 Spring Environment 에만 실리므로, 키가 설정으로 들어온 경우
    // 직접 넘겨준다. 비어 있으면 EC2 IAM 역할·실제 환경변수를 쓰는 기본 체인으로 폴백한다.
    private val credentialsProvider: AwsCredentialsProvider =
        if (properties.accessKey.isNotBlank() && properties.secretKey.isNotBlank()) {
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(properties.accessKey, properties.secretKey)
            )
        } else {
            DefaultCredentialsProvider.create()
        }

    private val presigner: S3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build()

    private val utilities: S3Utilities = S3Utilities.builder().region(region).build()

    override fun issue(key: String, contentType: String): UploadUrl {
        val presigned = presigner.presignPutObject { presign ->
            presign
                .signatureDuration(properties.presignedUrlTtl)
                .putObjectRequest { put -> put.bucket(properties.bucket).key(key).contentType(contentType) }
        }
        return UploadUrl(
            uploadUrl = presigned.url().toString(),
            imageUrl = utilities.getUrl { it.bucket(properties.bucket).key(key) }.toString(),
            expiresInSeconds = properties.presignedUrlTtl.seconds
        )
    }
}
