package com.challa.web.user

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import tools.jackson.core.JacksonException
import tools.jackson.module.kotlin.jacksonObjectMapper
import tools.jackson.module.kotlin.readValue

class UserDtosJsonTest {
    private val mapper = jacksonObjectMapper()

    @Test
    fun `profile response nests the payload under user`() {
        val json = mapper.writeValueAsString(UserEnvelope(UserProfileResponse(7, "닉", null)))

        assertEquals("""{"user":{"id":7,"nickname":"닉","profileImageUrl":null}}""", json)
    }

    @Test
    fun `profile update accepts a full body`() {
        val request: UserEnvelope<UpdateProfileRequest> =
            mapper.readValue("""{"user":{"nickname":"닉","profileImageUrl":"https://img.example/1.png"}}""")

        assertEquals("닉", request.user.nickname)
        assertEquals("https://img.example/1.png", request.user.profileImageUrl)
    }

    @Test
    fun `profile update accepts an explicit null image url`() {
        val request: UserEnvelope<UpdateProfileRequest> =
            mapper.readValue("""{"user":{"nickname":"닉","profileImageUrl":null}}""")

        assertNull(request.user.profileImageUrl)
    }

    @Test
    fun `profile update rejects a body missing any field`() {
        listOf(
            """{"user":{"nickname":"닉"}}""",
            """{"user":{"profileImageUrl":null}}""",
            """{"user":{}}""",
            """{"nickname":"닉","profileImageUrl":null}""",
            "{}"
        ).forEach { body ->
            assertThrows(JacksonException::class.java) {
                mapper.readValue<UserEnvelope<UpdateProfileRequest>>(body)
            }
        }
    }
}
