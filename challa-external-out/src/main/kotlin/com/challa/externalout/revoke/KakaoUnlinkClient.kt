package com.challa.externalout.revoke

import com.challa.externalout.config.SocialRevokeProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient

@Component
class KakaoUnlinkClient(properties: SocialRevokeProperties) {
    private val config = properties.kakao
    private val restClient = RestClient.create()

    fun unlink(providerUserId: String) {
        val form = LinkedMultiValueMap<String, String>().apply {
            add("target_id_type", "user_id")
            add("target_id", providerUserId)
        }
        restClient.post()
            .uri(config.unlinkUri)
            .header(HttpHeaders.AUTHORIZATION, "KakaoAK ${config.adminKey}")
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(form)
            .retrieve()
            .toBodilessEntity()
    }
}
