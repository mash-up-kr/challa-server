package com.challa.externalout.revoke

import com.challa.externalout.config.AppleRevoke
import com.nimbusds.jose.JWSAlgorithm
import com.nimbusds.jose.JWSHeader
import com.nimbusds.jose.crypto.ECDSASigner
import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import java.security.KeyFactory
import java.security.interfaces.ECPrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.time.Duration
import java.time.Instant
import java.util.Base64
import java.util.Date

class AppleClientSecretGenerator(private val apple: AppleRevoke) {
    fun generate(now: Instant = Instant.now()): String {
        val claims = JWTClaimsSet.Builder()
            .issuer(apple.teamId)
            .subject(apple.clientId)
            .audience("https://appleid.apple.com")
            .issueTime(Date.from(now))
            .expirationTime(Date.from(now.plus(Duration.ofMinutes(5))))
            .build()

        val header = JWSHeader.Builder(JWSAlgorithm.ES256).keyID(apple.keyId).build()
        val signedJwt = SignedJWT(header, claims)
        signedJwt.sign(ECDSASigner(loadPrivateKey(apple.privateKey)))
        return signedJwt.serialize()
    }

    private fun loadPrivateKey(pem: String): ECPrivateKey {
        val normalized = pem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replace("\\s".toRegex(), "")
        val decoded = Base64.getDecoder().decode(normalized)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        return KeyFactory.getInstance("EC").generatePrivate(keySpec) as ECPrivateKey
    }
}
