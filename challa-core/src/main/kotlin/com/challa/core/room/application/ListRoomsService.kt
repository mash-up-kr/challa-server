package com.challa.core.room.application

import com.challa.core.photo.PhotoRepository
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsCommand
import com.challa.core.room.port.input.ListRoomsResult
import com.challa.core.room.port.input.ListRoomsUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ListRoomsService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository,
    private val photoRepository: PhotoRepository
) : ListRoomsUsecase {
    override fun listRooms(listRoomsCommand: ListRoomsCommand): ListRoomsResult {
        val roomIds = roomUserRepository.findAllByUserId(listRoomsCommand.userId).map { it.roomId }
        val rooms = roomRepository.findAllById(roomIds)
        val now = LocalDateTime.now()
        val newlyCompletedRoomIds = rooms
            .filter { room ->
                room.roomStatus == RoomStatus.PHOTO_PRINT_PENDING &&
                    room.photoPrintCompletedAt?.isBefore(now) == true
            }
            .map { it.id!! }
        if (newlyCompletedRoomIds.isNotEmpty()) {
            val newlyCompletedRooms = roomRepository.updateRoomsStatus(
                roomIds = newlyCompletedRoomIds,
                roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED
            )
            roomRepository.saveAll(newlyCompletedRooms)
        }

        val newlyCompletedRoomIdSet = newlyCompletedRoomIds.toSet()
        val roomsWithUpdatedStatus = rooms.map { room ->
            if (room.id in newlyCompletedRoomIdSet) {
                room.copy(roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED)
            } else {
                room
            }
        }
        val requestedStatusSet = listRoomsCommand.status.toSet()
        val filteredRooms = roomsWithUpdatedStatus.filter { it.roomStatus in requestedStatusSet }
        val memberCountsByRoomId = roomUserRepository
            .countMembersByRoomIds(filteredRooms.map { it.id!! })
            .associate { it.roomId to it.memberCount }
        val photosByRoomId = photoRepository.findLatestFourByRoomIdsIn(roomIds).groupBy { it.roomId }
        val roomProjections = filteredRooms.map { room ->
            ListRoomsResult.RoomProjection(
                roomId = room.id!!,
                roomStatus = room.roomStatus,
                title = room.title,
                memberCount = memberCountsByRoomId.getValue(room.id),
                totalPhotoCount = room.totalPhotoCount,
                remainedPhotoCount = room.remainedPhotoCount,
                thumbnailImageUrls = photosByRoomId[room.id].orEmpty().map { it.imageUrl },
                photoPrintCompletedAt = room.photoPrintCompletedAt,
                createdAt = room.createdAt,
                expiresAt = room.expiresAt
            )
        }

        return ListRoomsResult(roomProjections = roomProjections)
    }
}
