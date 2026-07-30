---
name: writing-commits-and-prs
description: Use when committing changes or opening a pull request in this repository
---

# 커밋 / PR 작성

커밋 메시지와 PR 본문은 diff에 있는 사실만 담는다.

## 컨벤션

- 커밋 제목 = PR 제목 = `[TYPE] 한국어 요약` (예: `[FEAT] 공통 API 응답 및 비즈니스 예외 처리 추가`)
- TYPE: `FEAT` `FIX` `REFACTOR` `TEST` `DOCS` `CHORE` `DEPLOY`
- 브랜치는 `feature/<slug>` `fix/<slug>` `chore/<slug>`, base는 `develop`
- 제목으로 설명되면 커밋 본문은 비운다

확실하지 않으면 `git log --oneline -20`의 히스토리를 따른다.

## PR 본문

`.github/PULL_REQUEST_TEMPLATE.md`의 섹션을 그대로 채운다.

- 안내용 HTML 주석(`<!-- ... -->`)은 지운다. **내용을 HTML 주석 안에 쓰면 렌더링되지 않아 빈 섹션이 된다.**
- Changes는 diff에 있는 파일·클래스만 한 줄씩
- Related Issue는 이슈 번호가 있을 때만 `Closes #N`, 없으면 비운다
- Notes는 diff에 근거가 있는 사실만 — 설정·환경변수 변경, 이번 PR에서 의도적으로 남긴 한계, 리뷰어가 집중해서 볼 파일. 없으면 비운다

## 체크박스는 확인한 것만

직접 실행해서 결과를 본 항목만 `[x]`. 나머지는 `[ ]`로 남기고, 필요하면 Notes에 이유를 한 줄 쓴다.
`셀프 리뷰`와 `불필요한 코드와 로그를 제거했습니다`는 diff를 끝까지 읽었으면 체크한다 — 실행이 필요한 항목이 아니다.

| 넘어가려는 생각 | 사실 |
|---|---|
| "이 정도면 로컬에서 돌려봤을 것이다" | 내가 실행한 명령이 없으면 체크하지 않는다 |
| "조건부 항목이니 해당 없으면 체크해도 된다" | 해당 없음은 `[ ]`다 |

Red flags — 멈추고 다시 쓴다:

- 실행한 적 없는 항목이 `[x]`
- 본문에 diff에 없는 클래스·필드·계획이 등장

## PR 생성

본문은 파일에 쓰고 넘긴다.

```bash
gh pr create --base develop --title "[TYPE] 요약" --body-file pr-body.md
```

`--body`에 긴 마크다운을 인라인으로 넣으면 셸 이스케이프에서 깨진다.
