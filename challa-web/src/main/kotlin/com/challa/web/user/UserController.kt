package com.challa.web.user

import com.challa.core.user.DeleteAccountUseCase
import com.challa.core.user.GetProfileUseCase
import com.challa.core.user.UpdateProfileUseCase
import com.challa.web.common.response.ApiResponse
import com.challa.web.security.AuthUserId
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
    private val deleteAccountUseCase: DeleteAccountUseCase
) {
    @GetMapping("/me")
    fun me(@AuthUserId userId: Long): ApiResponse<UserEnvelope<UserProfileResponse>> =
        ApiResponse.ok(UserEnvelope(UserProfileResponse.from(getProfileUseCase.getProfile(userId))))

    @PutMapping("/me")
    fun updateMe(
        @AuthUserId userId: Long,
        @RequestBody request: UserEnvelope<UpdateProfileRequest>
    ): ApiResponse<UserEnvelope<UserProfileResponse>> = ApiResponse.ok(
        UserEnvelope(UserProfileResponse.from(updateProfileUseCase.update(userId, request.user.toCommand())))
    )

    @DeleteMapping("/me")
    fun deleteMe(@AuthUserId userId: Long): ApiResponse<Unit?> {
        deleteAccountUseCase.delete(userId)
        return ApiResponse.empty()
    }
}
