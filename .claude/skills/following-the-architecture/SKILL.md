---
name: following-the-architecture
description: Use when adding, moving, or renaming code in this repository, or deciding which module and package something belongs in
---

# 아키텍처

## 모듈과 의존 방향

```
bootstrap    →  web, external-in, external-out, persistence, core
web          →  core
external-out →  core
persistence  →  core
core         →  (없음)
```

`core`에 Spring·JPA·Jackson import가 등장하면 잘못된 위치다.

어댑터끼리는 서로 의존하지 않는다. `web`은 `persistence`를 직접 부르지 않고 `core`의 포트를 통한다.

## 패키지는 도메인 우선

모든 모듈이 `<모듈 루트>.<도메인>` 으로 나뉜다. 현재 도메인은 `auth`, `user`. 레이어(`entity`, `adapter`, `usecase`, `port`)로 먼저 나누지 않고, 도메인 안에서는
평평하게 둔다.

`src/test`도 같은 규칙을 따른다. 여러 도메인이 함께 쓰는 테스트 픽스처만 모듈 테스트 루트에 둔다 (`core/src/test/.../core/Fakes.kt`).

도메인에 속하지 않는 것만 별도 패키지를 쓴다 — `web.common`(응답·예외), `web.config`, `web.security`, `persistence.config`.

두 도메인이 함께 쓰는 타입은 공용 패키지를 만들지 말고 더 근본이 되는 도메인에 둔다. `Provider`는 `core.auth`에 있고 `core.user`가 import한다.

## 파일 이름

한 도메인의 같은 종류를 한 파일에 모은다. 예외를 개별 파일로 흩뜨리지 않는다.

```
<도메인>UseCases.kt    유스케이스 인터페이스 + 커맨드/결과
<도메인>Gateways.kt    나가는 포트
<도메인>Exceptions.kt  도메인 예외 전부
<도메인>Dtos.kt        요청·응답 DTO
```

클래스가 하나뿐인 파일은 그 클래스 이름을 파일명으로 쓴다. ktlint가 강제한다.

`core`의 유스케이스 구현은 `...Service`, 나가는 포트 구현은 `...Adapter`다. 유스케이스에 Spring 애노테이션을 달지 않고, 빈 등록은 `bootstrap`의
`CoreBeansConfig`가 생성자 주입으로 한다.

## 컨트롤러

- 경로는 `/api/v1/<도메인 복수형>` — `user` 도메인 → `/api/v1/users`
- 반환은 항상 `ApiResponse<T>`. `ResponseEntity`는 예외 핸들러만 쓴다
- **성공은 항상 200이다.** POST에도 201을 쓰지 않는다
- 요청·응답 DTO와 JSON 구조는 [`following-api-contract`](../following-api-contract/SKILL.md)을 따른다
- 파라미터 순서는 `@AuthUserId`, `@RequestBody`
- 도메인 ↔ DTO 변환은 DTO 쪽 `from()` / `toCommand()`에서 한다
- 컨트롤러는 분기하지 않는다. 조건이 필요하면 유스케이스로 옮긴다

## 응답과 예외

성공은 `ApiResponse.ok(data)` 또는 `ApiResponse.empty()`. 성공 `message`는 항상 `"OK"`이고, 엔드포인트마다 다른 성공 문구를 넣지 않는다.

실패는 `GlobalExceptionHandler`가 예외 타입별로 상태 코드를 정한다. 실패에 200을 반환하는 경로는 없다. 새 도메인 예외를 만들면 핸들러에 매핑을 추가한다 — 빠뜨리면 500으로 나간다.

인증 실패 응답에는 고정 문구를 쓴다. 예외 메시지에 라이브러리 내부 정보가 담길 수 있으므로 클라이언트로 내보내지 않고 로그에만 남긴다.

## 인증은 기본 차단

`/api/**` 의 모든 핸들러는 인증이 필요하다. 공개하려면 `@PublicEndpoint`를 붙인다. 새 엔드포인트는 아무것도 하지 않아도 보호된다.

OpenAPI의 bearer 표기는 `AuthenticationInterceptor.isPublic`을 공유하므로 문서를 따로 손대지 않는다.

## 컴포넌트 스캔은 모듈 루트로

`@EntityScan`, `@EnableJpaRepositories`는 `basePackages = ["com.challa.persistence"]` 형태로 쓴다. 메인 설정과 테스트 설정 모두.

`basePackageClasses = [UserEntity::class]`는 그 클래스의 패키지만 스캔한다. 도메인을 나누는 순간 다른 도메인의 엔티티가 등록되지 않고, **컴파일은 통과하고 테스트만 깨진다.**

## 스키마

`ddl-auto: update`라 새 `@Entity`는 테이블을 자동 생성한다. 마이그레이션 도구가 없으므로 컬럼 삭제·타입 변경은 반영되지 않는다. 기존 컬럼을 바꿔야 하면 PR에 수동 DDL을 적는다.
