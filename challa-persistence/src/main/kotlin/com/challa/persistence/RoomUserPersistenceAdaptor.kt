package com.challa.persistence

import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.output.RoomMemberCount
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.persistence.entity.RoomUserEntity
import com.challa.persistence.repository.RoomUserJpaRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class RoomUserPersistenceAdaptor(private val roomUserJpaRepository: RoomUserJpaRepository) : RoomUserRepository {
    override fun save(roomUser: RoomUser): RoomUser =
        roomUserJpaRepository.save(RoomUserEntity.from(roomUser)).toDomain()

    override fun insertIfAbsent(roomUser: RoomUser): Boolean = roomUserJpaRepository.insertIfAbsent(
        roomId = roomUser.roomId,
        userId = roomUser.userId,
        createdAt = roomUser.createdAt
    ) == 1

    override fun findAllByUserId(userId: Long): List<RoomUser> =
        roomUserJpaRepository.findAllByUserId(userId).map { it.toDomain() }

    override fun countMembersByRoomIds(roomIds: List<RoomId>): List<RoomMemberCount> =
        roomUserJpaRepository.countMembersByRoomIds(roomIds)

    override fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUser? =
        roomUserJpaRepository.findByUserIdAndRoomId(userId = userId, roomId = roomId)?.toDomain()

    override fun findAllByRoomId(roomId: Long): List<RoomUser> =
        roomUserJpaRepository.findAllByRoomId(roomId).map { it.toDomain() }

    @Transactional
    override fun markPhotoPrintCompletionChecked(userId: Long, roomId: Long, checkedAt: LocalDateTime) {
        roomUserJpaRepository.markPhotoPrintCompletionChecked(
            userId = userId,
            roomId = roomId,
            checkedAt = checkedAt
        )
    }

    @Transactional
    override fun deleteAllByRoomId(roomId: RoomId) {
        roomUserJpaRepository.deleteAllByRoomId(roomId)
    }
}
