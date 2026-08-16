package com.challa.core.room.application

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.input.GetRoomUsersCommand
import com.challa.core.room.port.input.GetRoomUsersResult
import com.challa.core.room.port.input.GetRoomUsersUsecase
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.user.UserRepository
import org.springframework.stereotype.Service

@Service
class GetRoomUsersService(
    private val roomUserRepository: RoomUserRepository,
    private val userRepository: UserRepository
) : GetRoomUsersUsecase {
    override fun getRoomUsers(getRoomUsersCommand: GetRoomUsersCommand): GetRoomUsersResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = getRoomUsersCommand.userId,
            roomId = getRoomUsersCommand.roomId
        ) ?: throw NoMatchingRoomException()

        val roomUsers = roomUserRepository.findAllByRoomId(getRoomUsersCommand.roomId)
        val userIds = roomUsers.map { it.userId }
        val users = userRepository.findAllByIds(userIds)
        val userProjections = users.map { user ->
            GetRoomUsersResult.UserProjection(
                id = user.id!!,
                nickname = user.nickname,
                profileImageUrl = user.profileImageUrl
            )
        }

        return GetRoomUsersResult(
            userProjections = userProjections
        )
    }
}
