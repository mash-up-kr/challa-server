---
name: following-api-contract
description: Use when adding, changing, or reviewing HTTP API controllers, request and response DTOs, ApiResponse payloads, JSON field names, or response nesting in this repository
---

# API 요청·응답 계약

## 계층과 변환 흐름

기존의 `Request → Command → Result → Response` 구조와 각 클래스의 역할을 유지한다.

- `Request`, `Response`, 도메인별 `Envelope`는 web 모듈에 둔다.
- `Command`, `Result`는 core 모듈에 둔다.
- Envelope는 HTTP 요청 본문을 받거나 HTTP 응답을 내리는 web 경계에서만 사용한다.
- Command와 Result를 Envelope로 감싸거나 web 모듈로 옮기지 않는다.
- Envelope 적용만을 이유로 기존 Request, Command, Result, Response 클래스를 삭제하거나 공용 DTO로 교체하지 않는다.
- JSON 필드 계약을 맞추는 데 필요한 Request와 Response 필드 변경은 허용한다.

전체 변환 흐름은 다음과 같다.

```text
DomainEnvelope<WebRequest>
  → WebRequest.toCommand()
  → Core Command
  → UseCase
  → Core Result
  → WebResponse.from()/fromResult()
  → DomainEnvelope<WebResponse>
  → ApiResponse
```

즉, 감싸는 대상은 HTTP 입출력의 Request와 Response다. Result를 Envelope에 직접 넣어 응답하지 않는다.

## 요청

요청 본문은 주 도메인의 단수형 `Envelope`로 감싸고, 그 아래에 실제 요청 DTO의 데이터를 바로 둔다.

```json
{
    "room": {
        "title": "Trip",
        "totalPhotoCount": 24
    }
}
```

컨트롤러는 `@RequestBody request: RoomEnvelope<CreateRoomRequest>`처럼 Envelope를 받고, `request.room.toCommand(userId)`로 변환한다. 요청
DTO 안에 도메인 필드를 다시 만들지 않는다.

다른 도메인을 참조하는 필드는 해당 도메인 이름을 유지한다.

```json
{
    "chat": {
        "roomId": 1,
        "photoId": 2,
        "content": "좋은 사진이에요"
    }
}
```

## 응답

모든 응답은 `ApiResponse<T>`로 반환하고 도메인별 `Envelope`를 `data`에 넣는다. Envelope 안에는 이름이 `Response`로 끝나는 web DTO를 넣는다.

```json
{
    "success": true,
    "message": "OK",
    "data": {
        "room": {
            "id": 1,
            "title": "Trip"
        }
    }
}
```

실제 데이터가 목록이어도 같은 단수형 Envelope를 사용한다. `data` 바로 아래에 실제 필드를 노출하거나 도메인을 생략하지 않는다.

## 중첩

도메인 아래에는 실제 데이터 외의 중간 래퍼를 추가하지 않는다.

```json
{
    "data": {
        "room": {
            "id": 1,
            "status": "SHOOTING"
        }
    }
}
```

`room.roomProjection`, `photo.photoDetail`처럼 표현 방식이나 조회 형태를 나타내는 중간 필드는 만들지 않는다.

다른 도메인의 소수 필드가 필요하면 별도 객체로 감싸지 않고 도메인 이름을 붙여 평탄화한다.

```json
{
    "id": 1,
    "content": "좋은 사진이에요",
    "userName": "찰라",
    "userProfileImageUrl": "https://example.com/profile.jpg"
}
```

## 목록

주 도메인 자체의 목록도 `data` 아래에 단수형 도메인 이름을 만들고 배열을 바로 둔다.

```json
{
    "data": {
        "room": [
            {
                "id": 1,
                "title": "Trip"
            }
        ]
    }
}
```

`data.room: {}`은 단건, `data.room: []`는 목록이다. 데이터 형태와 관계없이 도메인 키는 항상 단수형이다. `data.room.rooms`, `data.room.roomProjections`
처럼 도메인과 실제 데이터 사이에 중간 필드를 추가하지 않는다.

상세·복합 데이터에 포함된 목록은 의미가 드러나는 복수형 필드명을 유지한다. 예를 들어 사진 상세의 `chats`, 촬영 정보의 `cameraFilters`는 유지한다.

## 필드명

필드가 현재 도메인 자체를 나타내면 필드명에서 도메인 이름을 제거한다.

- `room.roomId` → `room.id`
- `room[].roomId` → `room[].id`
- `room.roomStatus` → `room.status`
- `room.roomTitle` → `room.title`
- `photo.photoId` → `photo.id`

목록을 담은 단수형 도메인 아래의 각 항목도 같은 도메인으로 판단한다. 따라서 `room` 배열의 각 항목에서 `roomId`는 `id`로 바꾼다.

필드가 다른 도메인을 나타내면 구분을 위해 도메인 이름을 유지한다.

- `room.userId`는 그대로 둔다.
- `chat.roomId`는 그대로 둔다.
- `chat.photoId`는 그대로 둔다.
- `chat.userName`, `chat.userProfileImageUrl`은 그대로 둔다.

필드명을 정하기 전에 현재 데이터를 감싸는 주 도메인과 필드가 실제로 가리키는 도메인을 먼저 구분한다.

## Envelope와 DTO

- 각 도메인에 generic Envelope를 만든다.
- Envelope는 단수형 도메인 필드를 갖는다.
- 단건과 목록에 같은 Envelope를 사용한다.
- 요청과 응답의 도메인 wrapping은 모두 Envelope로 처리한다.
- 요청 Envelope 안에는 이름이 `Request`로 끝나는 요청 DTO를 넣는다.
- 응답 Envelope 안에는 이름이 `Response`로 끝나는 응답 DTO 또는 그 목록을 넣는다.
- core의 Result나 도메인 객체를 응답 Envelope에 직접 넣지 않는다. Envelope의 직접 값은 web Response여야 한다.
- 응답 DTO는 기존 변환 방식인 `from()` 또는 `fromResult()`에서 Result나 도메인 객체를 변환한다.
- 응답 DTO에는 실제 응답 필드만 두고 도메인 wrapper 필드를 다시 만들지 않는다.
- 기존 Response가 중첩 core 타입을 필드로 사용하고 있었다면 JSON 계약상 변경이 필요하지 않은 한 이를 복제한 중첩 Response DTO를 새로 만들지 않는다.
- Envelope가 기존 Response의 도메인 wrapper 역할을 대신하더라도 기존 Response 클래스 자체는 유지하고 실제 데이터 표현 역할로 조정한다.
- Envelope는 도메인 wrapping 역할이므로 `Response`로 이름을 바꾸지 않는다.
- 기존 Request, Command, Result, Response 클래스 이름과 계층은 유지한다.
- 요청 변환은 Envelope 안의 요청 DTO가 제공하는 `toCommand()`에서 처리한다.

```kotlin
data class RoomEnvelope<T : Any>(val room: T)

data class CreateRoomResponse(val invitationCode: String) {
    companion object {
        fun fromResult(result: CreateRoomResult) = CreateRoomResponse(
            invitationCode = result.invitationCode
        )
    }
}

fun createRoom(
    @AuthUserId userId: Long,
    @RequestBody request: RoomEnvelope<CreateRoomRequest>
): ApiResponse<RoomEnvelope<CreateRoomResponse>> {
    val result = createRoomUsecase.createRoom(request.room.toCommand(userId))
    return ApiResponse.ok(RoomEnvelope(CreateRoomResponse.fromResult(result)))
}

val result: ListRoomsResult = listRoomsUsecase.listRooms(command)
val response = ApiResponse.ok(RoomEnvelope(result.roomProjections.map(ListRoomsResponse::fromResult)))
```

## 빈 성공 응답

반환할 실제 데이터가 없으면 `ApiResponse.ok(Unit)` 대신 `ApiResponse.empty()`를 사용한다.

## 점검

구현을 마치면 다음을 확인한다.

- 요청 최상위가 주 도메인으로 감싸졌는가?
- 요청과 응답의 도메인 wrapping에 도메인별 Envelope를 사용했는가?
- Envelope가 web 경계에만 있고 core Command와 Result에는 들어가지 않았는가?
- 기존 Request → Command → Result → Response 변환 구조와 클래스 역할을 유지했는가?
- 응답 데이터가 `ApiResponse.data` 아래의 주 도메인 안에 있는가?
- 단건과 목록 모두 단수형 도메인 Envelope를 사용했는가?
- 응답 Envelope 안의 클래스 이름이 `Response`로 끝나는가?
- core Result나 도메인 객체를 직접 응답하고 있지 않은가?
- 기존 중첩 타입을 복제한 불필요한 Response DTO를 추가하지 않았는가?
- 도메인 아래에 불필요한 중간 래퍼가 없는가?
- 현재 도메인을 반복하는 필드명은 제거했는가?
- 다른 도메인을 나타내는 필드명은 유지했는가?
- 주 도메인 목록은 단수형 도메인 바로 아래 배열인가?
- 기존 컨트롤러 테스트의 JSON 경로를 변경된 계약에 맞췄는가?
