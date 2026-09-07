package com.challa.core.photo

import com.challa.core.room.exception.NoMatchingRoomException
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.core.user.User
import com.challa.core.user.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class ListPhotosService(
    private val roomUserRepository: RoomUserRepository,
    private val photoRepository: PhotoRepository,
    private val userRepository: UserRepository
) : ListPhotosUseCase {
    override fun listPhotos(command: ListPhotosCommand): ListPhotosResult {
        roomUserRepository.findByUserIdAndRoomId(
            userId = command.userId,
            roomId = command.roomId
        ) ?: throw NoMatchingRoomException()

        val pageable = PageRequest.of(
            command.page,
            command.size,
            Sort.by(Sort.Direction.ASC, "createdAt")
        )
        val photoSlice = photoRepository.findSliceByRoomId(command.roomId, pageable)
        val photos = photoSlice.photos
        val usersById = userRepository.findAllByIds(photos.map { it.userId }.distinct()).associateBy { it.id }
        val photoProjections = photos.sortedBy { it.createdAt }.map { photo ->
            val user = usersById[photo.userId]
            ListPhotosResult.PhotoProjection(
                id = requireNotNull(photo.id),
                imageUrl = photo.imageUrl,
                userNickname = User.displayNicknameOf(user),
                userProfileImageUrl = user?.profileImageUrl,
                createdAt = photo.createdAt!!
            )
        }

        return ListPhotosResult(
            photoProjections = photoProjections,
            hasNext = photoSlice.hasNext
        )
    }
}
