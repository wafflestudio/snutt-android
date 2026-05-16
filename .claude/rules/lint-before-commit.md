---
description: git commit 전 항상 ktlintFormat 과 dev variant 컴파일 검증을 실행하고, lint 자동 수정 결과는 동일 commit 에 포함시킨다.
alwaysApply: true
---

# Verify before commit

`git commit` 을 실행하기 직전에 항상 다음을 한 번에 실행한다:

```
./gradlew \
    ktlintFormat \
    compileDevDebugUnitTestKotlin \
    compileDevDebugScreenshotTestKotlin
```

ktlintFormat 결과로 변경이 발생하면 그 변경도 동일 commit 에 포함시킨다
(별도 commit 으로 쪼개지 않는다).

## Why

- 프로젝트 ktlint 규칙 위반을 commit 에 박지 않기 위해. 사람이 매번 기억할
  필요 없도록.
- ViewModel/Repository 등의 시그니처를 바꾸면 `src/test` 의 동명 `*Test.kt`
  나 screenshot test 의 preview 가 깨질 수 있다. 컴파일까지만 돌려도 이 종류의
  회귀는 거의 다 잡힌다.
- main 소스셋 컴파일만 확인하고 넘어가면 `src/test` 의 사용처는 그대로 안 잡혀
  PR push 후 CI 에서 처음 발견되는 일이 반복된다.
- live 가 아닌 dev variant 만으로 충분하다 — CI 가 dev 을 기준으로
  돌고, source set 차이는 dev 에서 모두 드러난다.

## How to apply

1. 코드 변경 후 commit 직전 (`git add` 직전) 위 gradle 명령을 한 번에 실행.
2. ktlintFormat 결과로 추가 변경이 발생하면 그것까지 staging.
3. 컴파일이 깨진 곳이 있으면 commit 멈추고 수정. 자동 우회하지 않는다.
4. ktlintFormat 이 자동 수정할 수 없는 lint 오류 (예: 명시적 변수명 위반,
   복잡 구조 등) 가 있어도 **commit 을 멈추고 사용자에게 알린다.** 자동으로
   우회하지 않는다.
5. 시그니처 변경이 있었다면, 컴파일 외에 영향받는 테스트의 실제 실행
   (`testDevDebugUnitTest --tests "..."`) 까지 한 번 돌려본다.

## Exceptions

- 사용자가 명시적으로 "검증 건너뛰어" / "lint 건너뛰어" 또는 비슷한 요청을 한
  경우 한정 skip.
- docs 만 변경하는 commit (.md 파일만): 빌드 영향 없음. skip 가능.
