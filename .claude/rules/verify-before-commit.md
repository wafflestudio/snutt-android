---
description: git commit 전 항상 unit test / screenshot test 의 컴파일을 live + staging variant 모두에 대해 검증한다.
alwaysApply: true
---

# Verify before commit

`git commit` 을 실행하기 직전, lint 와 함께 다음 컴파일을 모두 통과시킨다:

```
./gradlew \
    compileLiveDebugUnitTestKotlin compileStagingDebugUnitTestKotlin \
    compileLiveDebugScreenshotTestKotlin
```

## Why

- ViewModel/Repository 등의 시그니처를 바꾸면 `src/test` 의 동명 `*Test.kt`
  나 screenshot test 의 preview 가 깨질 수 있다. 컴파일까지만 돌려도 이 종류의
  회귀는 거의 다 잡힌다.
- live variant 만 검증하면 staging variant 의 source set 차이로 staging 빌드만
  깨지는 경우를 놓친다 (CI 가 staging 도 돌리므로 결국 빨간 불).
- main 소스셋 컴파일만 확인하고 넘어가면 `src/test` 의 사용처는 그대로 안 잡혀
  PR push 후 CI 에서 처음 발견되는 일이 반복된다.

## How to apply

1. 코드 변경 후 commit 직전 (lint 와 같은 시점) 위 gradle 명령을 한 번에 실행.
2. 깨진 곳이 있으면 commit 멈추고 수정. 자동 우회하지 않는다.
3. 시그니처 변경이 있었다면, 컴파일 외에 영향받는 테스트의 실제 실행
   (`testLiveDebugUnitTest --tests "..."`) 까지 한 번 돌려본다.

## Exceptions

- 사용자가 명시적으로 "검증 건너뛰어" 또는 비슷한 요청을 한 경우 한정 skip.
- docs 만 변경하는 commit (.md 파일만): 빌드 영향 없음. skip 가능.