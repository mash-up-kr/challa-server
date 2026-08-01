package com.challa.bootstrap

import com.challa.core.auth.LoginUseCase
import com.challa.core.auth.RefreshTokenUseCase
import com.challa.core.upload.IssueUploadUrlUseCase
import com.challa.core.user.DeleteAccountUseCase
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class ChallaApplicationTests {

    @Autowired
    private lateinit var loginUseCase: LoginUseCase

    @Autowired
    private lateinit var refreshTokenUseCase: RefreshTokenUseCase

    @Autowired
    private lateinit var deleteAccountUseCase: DeleteAccountUseCase

    @Autowired
    private lateinit var issueUploadUrlUseCase: IssueUploadUrlUseCase

    @Test
    fun `context loads and wires the core use cases with their adapters`() {
        assertNotNull(loginUseCase)
        assertNotNull(refreshTokenUseCase)
        assertNotNull(deleteAccountUseCase)
        assertNotNull(issueUploadUrlUseCase)
    }
}
