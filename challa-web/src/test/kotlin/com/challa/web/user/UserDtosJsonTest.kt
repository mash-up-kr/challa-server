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
    fun `profile update accepts a full body`() {
        val request: UpdateProfileRequest =
            mapper.readValue("""{"nickname":"닉","profileImageUrl":"https://img.example/1.png"}""")

        assertEquals("닉", request.nickname)
        assertEquals("https://img.example/1.png", request.profileImageUrl)
    }

    @Test
    fun `profile update accepts an explicit null image url`() {
        val request: UpdateProfileRequest =
            mapper.readValue("""{"nickname":"닉","profileImageUrl":null}""")

        assertNull(request.profileImageUrl)
    }

    @Test
    fun `profile update rejects a body missing any field`() {
        listOf("""{"nickname":"닉"}""", """{"profileImageUrl":null}""", "{}").forEach { body ->
            assertThrows(JacksonException::class.java) {
                mapper.readValue<UpdateProfileRequest>(body)
            }
        }
    }
}
