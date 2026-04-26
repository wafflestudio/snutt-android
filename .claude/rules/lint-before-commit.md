---
description: git commit 전 항상 ktlint 자동 수정을 실행하고 그 결과를 동일 commit 에 포함시킨다.
alwaysApply: true
---

# Lint before commit

`git commit` 을 실행하기 직전에 항상 `./gradlew ktlintFormat` 을 실행한다.
format 이후 변경이 발생하면 그 변경도 동일 commit 에 포함시킨다 (별도 commit
으로 쪼개지 않는다).

## Why

- 프로젝트 ktlint 규칙 위반을 commit 에 박지 않기 위해.
- 매번 사람이 lint 실행을 기억할 필요 없도록.
- CI 의 lint 검사를 미리 패스시켜 PR 단계의 review feedback loop 을 단축.

## How to apply

1. 코드 변경을 staging 하기 전 (`git add` 직전) 에 `./gradlew ktlintFormat`
   실행.
2. format 결과로 추가 변경이 발생하면 그것까지 staging.
3. format 이 자동 수정할 수 없는 lint 오류가 있으면 (예: 명시적 변수명 위반,
   복잡 구조 등) **commit 을 멈추고 사용자에게 알린다.** 자동으로 우회하지
   않는다.
4. ktlintFormat 이 실패하는 (Gradle 자체 오류) 경우도 commit 멈추고 보고.

## Exceptions

- 사용자가 명시적으로 "lint 건너뛰어" 또는 비슷한 요청을 한 경우 한정 skip.
- docs 만 변경하는 commit (.md 파일만) 의 경우 ktlint 대상 외라 영향 없지만
  실행 자체는 빠르므로 그대로 실행한다.
