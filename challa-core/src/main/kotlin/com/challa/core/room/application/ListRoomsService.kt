package com.challa.core.room.application

import com.challa.core.photo.PhotoRepository
import com.challa.core.room.domain.Room
import com.challa.core.room.domain.RoomCover
import com.challa.core.room.domain.RoomStatus
import com.challa.core.room.port.input.ListRoomsCommand
import com.challa.core.room.port.input.ListRoomsResult
import com.challa.core.room.port.input.ListRoomsUsecase
import com.challa.core.room.port.output.RoomCoverStickerProvider
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class ListRoomsService(
    private val roomRepository: RoomRepository,
    private val roomUserRepository: RoomUserRepository,
    private val photoRepository: PhotoRepository,
    private val roomCoverStickerProvider: RoomCoverStickerProvider
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
            roomRepository.updateRoomsStatus(
                roomIds = newlyCompletedRoomIds,
                roomStatus = RoomStatus.PHOTO_PRINT_COMPLETED
            )
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

        // 정렬 기준
        // 1차: 방 상태 우선순위
        //   1. PHOTO_PRINT_COMPLETED - 인화 완료
        //   2. SHOOTING              - 촬영 가능
        //   3. PHOTO_PRINT_PENDING   - 인화 대기
        //
        // 2차: 동일 상태 내 시간 기준 정렬
        //   - PHOTO_PRINT_COMPLETED: 인화 완료 시각 기준 최신순
        //   - SHOOTING: 생성 시각 기준 최신순
        //   - PHOTO_PRINT_PENDING: 인화 대기 상태 진입 시각 기준 오래된 순
        val sortedRooms = filteredRooms.sortedWith(
            compareBy<Room> {
                when (it.roomStatus) {
                    RoomStatus.PHOTO_PRINT_COMPLETED -> 1
                    RoomStatus.SHOOTING -> 2
                    RoomStatus.PHOTO_PRINT_PENDING -> 3
                }
            }
                .thenByDescending {
                    when (it.roomStatus) {
                        RoomStatus.PHOTO_PRINT_COMPLETED -> it.photoPrintCompletedAt

                        RoomStatus.SHOOTING -> it.createdAt

                        RoomStatus.PHOTO_PRINT_PENDING -> -it.photoPrintCompletedAt!!.toEpochSecond(ZoneOffset.UTC)
                    }
                }
        )
        val memberCountsByRoomId = roomUserRepository
            .countMembersByRoomIds(sortedRooms.map { it.id!! })
            .associate { it.roomId to it.memberCount }
        val photosByRoomId = photoRepository.findLatestFourByRoomIdsIn(roomIds).groupBy { it.roomId }
        val roomProjections = sortedRooms.map { room ->
            val coverSticker = room.coverStickerId?.let {
                val sticker = roomCoverStickerProvider.findStickerById(it)!!
                val color = roomCoverStickerProvider.findColorById(room.coverStickerColorId!!)!!

                RoomCover.Sticker(
                    id = sticker.id,
                    imageUrl = sticker.fileUrl,
                    color = RoomCover.Sticker.Color(
                        id = color.id,
                        name = color.name,
                        hex = color.hex
                    )
                )
            }

            ListRoomsResult.RoomProjection(
                roomId = room.id!!,
                roomStatus = room.roomStatus,
                title = room.title,
                memberCount = memberCountsByRoomId.getValue(room.id),
                totalPhotoCount = room.totalPhotoCount,
                remainedPhotoCount = room.remainedPhotoCount,
                thumbnailImageUrls = photosByRoomId[room.id].orEmpty().map { it.imageUrl },
                cover = RoomCover(
                    coverImageUrl = room.coverImageUrl,
                    sticker = coverSticker
                ),
                photoPrintCompletedAt = room.photoPrintCompletedAt,
                createdAt = room.createdAt,
                expiresAt = room.expiresAt
            )
        }

        return ListRoomsResult(roomProjections = roomProjections)
    }
}
