---
name: writing-commits
description: Use when writing a commit message or naming a branch in this repository
---

# 커밋 메시지

- 제목: `[TYPE] 한국어 요약` (예: `[FEAT] 공통 API 응답 및 비즈니스 예외 처리 추가`)
- TYPE: `FEAT` `FIX` `REFACTOR` `TEST` `DOCS` `CHORE` `DEPLOY`
- 제목으로 설명되면 본문은 비운다. 안 되면 "왜"를 bullet 2~3개로
- 브랜치는 `feature/<slug>` `fix/<slug>` `chore/<slug>`, base는 `develop`

한 커밋에는 한 가지만 담는다. 리팩터링과 기능 추가가 섞이면 나눈다.

확실하지 않으면 `git log --oneline -20`의 히스토리를 따른다.

PR 제목도 같은 형식이다. PR 본문 작성은 `writing-pull-requests`를 쓴다.
