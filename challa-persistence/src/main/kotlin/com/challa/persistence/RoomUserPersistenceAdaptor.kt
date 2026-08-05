package com.challa.persistence

import com.challa.core.room.domain.RoomId
import com.challa.core.room.domain.RoomUser
import com.challa.core.room.port.output.RoomMemberCount
import com.challa.core.room.port.output.RoomUserRepository
import com.challa.persistence.entity.RoomUserEntity
import com.challa.persistence.repository.RoomUserJpaRepository
import org.springframework.stereotype.Component

@Component
class RoomUserPersistenceAdaptor(private val roomUserJpaRepository: RoomUserJpaRepository) : RoomUserRepository {
    override fun save(roomUser: RoomUser): RoomUser =
        roomUserJpaRepository.save(RoomUserEntity.from(roomUser)).toDomain()

    override fun findAllByUserId(userId: Long): List<RoomUser> =
        roomUserJpaRepository.findAllByUserId(userId).map { it.toDomain() }

    override fun countMembersByRoomIds(roomIds: List<RoomId>): List<RoomMemberCount> =
        roomUserJpaRepository.countMembersByRoomIds(roomIds)

    override fun findByUserIdAndRoomId(userId: Long, roomId: Long): RoomUser? =
        roomUserJpaRepository.findByUserIdAndRoomId(userId = userId, roomId = roomId)?.toDomain()

    override fun findAllByRoomId(roomId: Long): List<RoomUser> =
        roomUserJpaRepository.findAllByRoomId(roomId).map { it.toDomain() }
}
