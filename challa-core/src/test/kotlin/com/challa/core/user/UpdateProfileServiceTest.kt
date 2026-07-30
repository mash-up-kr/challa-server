package com.challa.core.user

import com.challa.core.FakeUserRepository
import com.challa.core.auth.Provider
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateProfileServiceTest {
    private lateinit var users: FakeUserRepository
    private lateinit var service: UpdateProfileService
    private var writesBefore = 0

    @BeforeEach
    fun setUp() {
        users = FakeUserRepository()
        service = UpdateProfileService(users)
        users.save(
            User(
                id = 1,
                provider = Provider.KAKAO,
                providerId = "kakao-sub",
                nickname = "이전 닉네임",
                profileImageUrl = "https://img.example/old.png"
            )
        )
        writesBefore = users.saveCount
    }

    @Test
    fun `replaces every profile field`() {
        val updated = service.update(
            1,
            UpdateProfileCommand(nickname = "새 닉네임", profileImageUrl = "https://img.example/new.png")
        )

        assertEquals("새 닉네임", updated.nickname)
        assertEquals("https://img.example/new.png", updated.profileImageUrl)
        assertEquals(updated, users.findById(1))
    }

    @Test
    fun `a null image url clears the stored image instead of keeping it`() {
        val updated = service.update(1, UpdateProfileCommand(nickname = "새 닉네임", profileImageUrl = null))

        assertNull(updated.profileImageUrl)
        assertNull(users.findById(1)!!.profileImageUrl)
    }

    @Test
    fun `a blank image url is stored as null`() {
        val updated = service.update(1, UpdateProfileCommand(nickname = "새 닉네임", profileImageUrl = "   "))

        assertNull(updated.profileImageUrl)
    }

    @Test
    fun `rejects a non-http image url`() {
        listOf("javascript:alert(1)", "data:text/html;base64,AAAA", "img.example/a.png").forEach { url ->
            assertThrows(InvalidProfileImageUrlException::class.java) {
                service.update(1, UpdateProfileCommand("새 닉네임", url))
            }
        }
        assertEquals(writesBefore, users.saveCount)
    }

    @Test
    fun `normalizes the nickname before storing it`() {
        val updated = service.update(1, UpdateProfileCommand("  용감한   호랑이  ", null))

        assertEquals("용감한 호랑이", updated.nickname)
    }

    @Test
    fun `rejects a blank nickname`() {
        assertThrows(InvalidNicknameException::class.java) {
            service.update(1, UpdateProfileCommand("   ", null))
        }

        assertEquals("이전 닉네임", users.findById(1)!!.nickname)
        assertEquals(writesBefore, users.saveCount)
    }

    @Test
    fun `rejects an invisible nickname so onboarding cannot be faked`() {
        val invisibleCodePoints = listOf(0x200B, 0x3164, 0xFEFF)
        invisibleCodePoints.map { String(Character.toChars(it)) }.forEach { invisible ->
            assertThrows(InvalidNicknameException::class.java) {
                service.update(1, UpdateProfileCommand(invisible, null))
            }
        }
        assertEquals("이전 닉네임", users.findById(1)!!.nickname)
        assertEquals(writesBefore, users.saveCount)
    }

    @Test
    fun `rejects a nickname over the length limit`() {
        val tooLong = "가".repeat(NicknamePolicy.MAX_LENGTH + 1)

        assertThrows(InvalidNicknameException::class.java) {
            service.update(1, UpdateProfileCommand(tooLong, null))
        }
    }

    @Test
    fun `rejects an unknown user`() {
        assertThrows(UserNotFoundException::class.java) {
            service.update(999, UpdateProfileCommand("새 닉네임", null))
        }
    }

    @Test
    fun `an unchanged profile is not rewritten`() {
        val before = users.findById(1)!!

        val updated = service.update(
            1,
            UpdateProfileCommand(nickname = "이전 닉네임", profileImageUrl = "https://img.example/old.png")
        )

        assertEquals(before, updated)
        assertEquals(writesBefore, users.saveCount)
    }

    @Test
    fun `a real change is written exactly once`() {
        service.update(1, UpdateProfileCommand("새 닉네임", null))

        assertEquals(writesBefore + 1, users.saveCount)
    }
}
