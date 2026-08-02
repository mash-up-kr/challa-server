package com.challa.core.room.application

import com.challa.core.room.port.input.GetShootableRoomsCommand
import com.challa.core.room.port.input.GetShootableRoomsResult
import com.challa.core.room.port.input.GetShootableRoomsUsecase
import com.challa.core.room.port.output.RoomRepository
import com.challa.core.room.port.output.RoomUserRepository
import org.springframework.stereotype.Service

@Service
class GetShootableRoomsService(
    private val roomUserRepository: RoomUserRepository,
    private val roomRepository: RoomRepository
) : GetShootableRoomsUsecase {
    override fun getShootableRooms(getShootableRoomsCommand: GetShootableRoomsCommand): GetShootableRoomsResult {
        val userId = getShootableRoomsCommand.userId
        val participatedRooms = roomUserRepository.findAllByUserId(userId)
        val rooms = roomRepository.findAllById(participatedRooms.map { it.roomId })
        val shootableRooms = rooms.filter { it.remainedPhotoCount > 0 }.map { room ->
            GetShootableRoomsResult.ShootableRoom(
                id = room.id!!,
                title = room.title,
                remainedPhotoCount = room.remainedPhotoCount,
                totalPhotoCount = room.totalPhotoCount
            )
        }

        return GetShootableRoomsResult(
            rooms = shootableRooms
        )
    }
}
