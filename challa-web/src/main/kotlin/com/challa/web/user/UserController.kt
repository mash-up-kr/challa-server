package com.challa.web.user

import com.challa.core.port.inbound.DeleteAccountUseCase
import com.challa.core.port.inbound.GetProfileUseCase
import com.challa.core.port.inbound.SuggestNicknameUseCase
import com.challa.core.port.inbound.UpdateProfileUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
import com.challa.web.security.PublicEndpoint
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val getProfileUseCase: GetProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val suggestNicknameUseCase: SuggestNicknameUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase
) {
    @GetMapping("/me")
    fun me(@AuthUserId userId: Long): ApiResponse<UserProfileResponse> {
        val user = getProfileUseCase.getProfile(userId)
        return ApiResponse.ok(UserProfileResponse.from(user))
    }

    @PutMapping("/me")
    fun updateMe(
        @AuthUserId userId: Long,
        @RequestBody request: UpdateProfileRequest
    ): ApiResponse<UserProfileResponse> {
        val user = updateProfileUseCase.update(userId, request.toCommand())
        return ApiResponse.ok(UserProfileResponse.from(user), message = "Profile updated")
    }

    @PublicEndpoint
    @GetMapping("/nickname/random")
    fun randomNickname(): ApiResponse<RandomNicknameResponse> =
        ApiResponse.ok(RandomNicknameResponse(suggestNicknameUseCase.suggest()))

    @DeleteMapping("/me")
    fun deleteMe(@AuthUserId userId: Long): ApiResponse<Unit?> {
        deleteAccountUseCase.delete(userId)
        return ApiResponse.empty("Account deleted")
    }
}
