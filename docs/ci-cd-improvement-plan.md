# CI/CD 정비 및 자동화 고도화 계획

## 목적

현재 `.github/workflows/` 의 워크플로우(`ci.yml`, `cd.yml`, `manual_deploy.yml`)를 정비하고, 자동화를 점진적으로 확장한다.

원칙:

- **낮은 리스크 / 높은 효과** 부터 먼저. 기반 정비(중복 제거·속도 개선) 이후에 고도화(자동화 추가).
- 각 Phase는 독립된 PR 단위로 쪼갠다. Phase 내부에서도 항목별로 커밋/PR 가능.
- 도입 비용이 큰 항목은 "검토 → 도입" 2단계로 나눈다.

---

## 현재 상태 스냅샷

- `ci.yml`: `develop` PR/push 시 ktlint → assembleStagingDebug → testStagingDebugUnitTest (직렬).
- `cd.yml`: `release-*` 브랜치의 `version.properties` 변경 시 live/staging 빌드 + Firebase + Slack.
- `manual_deploy.yml`: `workflow_dispatch` 로 live/staging 선택 배포.
- Java: `ci.yml` 은 21, `cd.yml`/`manual_deploy.yml` 은 17 (불일치).
- Gradle 캐시: `actions/setup-java` 의 `cache: gradle` 만 사용 중.
- Secrets 주입: heredoc (`cat << EOF > ... ${{ secrets.* }} EOF`) 방식. 3개 워크플로우에 거의 동일한 블록이 중복.

---

## Phase 1. 기반 정비 (리스크 낮음, 효과 빠름)

### [x] 1-1. Java 버전 통일

- 현재: CI 21, CD/Manual 17.
- 할 일: 셋 다 동일 버전으로 맞춘다. (앱 실제 빌드 환경과의 정합성 확인 필요 — `gradle/libs.versions.toml`, `build.gradle.kts` 의 `jvmToolchain` / `compileOptions` 기준으로 결정)
- 리스크: 낮음. 실제 요구되는 JDK 확인만 정확히.
- 결정/결과:
    - 근거: `app/build.gradle.kts` 의 `compileOptions.sourceCompatibility = VERSION_17` 과 정합 맞춤. JDK 21 고유 기능 미사용.
    - 조치: `ci.yml` java-version 21 → 17. `manual_deploy.yml` distribution `adopt`(legacy) → `temurin` 도 함께 정렬.
    - 별도 발견: `core/network` 모듈은 실제 존재하지 않음. 3개 워크플로우의 `./core/network/...` secrets 셋업 스텝은 쓸모없는 디렉토리만 생성. **1-2 에서 정리**.

### [x] 1-2. Secrets 셋업 composite action 추출

- 현재: google-services.json / secrets.xml(app, core:network) / gcp-service-account.json / keystore 가 3개 워크플로우에 거의 동일하게 중복.
- 할 일: `.github/actions/setup-secrets/action.yml` composite action 으로 뽑아 공통화. `variant` (staging/live) 를 입력으로 받아 분기.
- 효과: 한 곳만 수정하면 됨. 휴먼 에러 감소.
- 결정/결과:
    - `.github/actions/setup-secrets/action.yml` composite action 신설. inputs: `variant`, `google_services_json`, `secrets_xml_app`, `gcp_service_account`(optional), `keystore_base64`(optional).
    - `ci.yml` / `cd.yml` / `manual_deploy.yml` 의 중복 secrets 셋업 블록 전부 composite action 호출 한 스텝으로 치환.
    - 내부 구현은 `env:` 로 시크릿을 스텝에 주입하고 `printf '%s' "$VAR" > ...` 로 파일에 기록. heredoc 미사용(1-5 목표가 여기서 해소 — 별도 PR 불필요).
    - 실제 존재하지 않는 `core/network` 모듈용 secrets.xml 셋업 스텝 모두 제거. 관련 GitHub Secret (`secrets_xml_staging_core_network`, `secrets_xml_live_core_network`) 은 현재 참조되지 않음. (UI 에서 수동 제거 가능)
    - `manual_deploy.yml` 의 `startsWith(inputs.variant, 'live')` 조건은 값이 고정 enum 이라 `== 'live'` 로 단순화.
    - 별도 발견: `app/src/staging/res/value/strings.xml` 디렉토리 오타(`value` 단수형). Android 가 리소스로 인식 안 함. CI/CD 범위 밖이라 플래그만.

### [x] 1-3. Gradle 셋업 개선

- 현재: `actions/setup-java@v4` 의 `cache: gradle`.
- 할 일: `gradle/actions/setup-gradle@v4` 도입. build-cache / configuration-cache / dependency 캐시 전략 개선.
- 효과: 캐시 히트 향상 → CI 시간 단축.
- 결정/결과:
    - 3개 워크플로우 모두 `setup-java` 의 `cache: 'gradle'` 제거 후 `gradle/actions/setup-gradle@v4` 별도 스텝 추가.
    - `ci.yml` 에 한해 `cache-read-only: ${{ github.event_name == 'pull_request' }}` 지정. PR 에서는 캐시 write 금지하여 caching poisoning 방지, develop push 시만 쓰기.
    - 도입 직후 최초 실행은 캐시 미스로 느릴 수 있으나 이후 점진적으로 히트율 상승 예상.

### [x] 1-4. CI job 분리 (병렬화)

- 현재: ktlint → build → unit test 직렬.
- 할 일: ktlint / build / unit test 를 별도 job 으로 분리. 독립 실행 가능한 것은 병렬화.
- 효과: 실패 원인 분명, 빠른 피드백.
- 고려: Gradle 캐시/warm-up 중복 비용과의 트레이드오프 — 병렬화해도 실제로 빨라지는지 실측 필요.
- 결정/결과:
    - **2 job 분리** (`ktlint` / `build-and-test`). 3 job 완전 분리는 setup 오버헤드 × 3 + compile 중복으로 오히려 느려질 가능성 높음.
    - `build-and-test` 는 `assembleStagingDebug testStagingDebugUnitTest` 를 한 번에 실행 → compile 산출물 공유.
    - 테스트 실패 시 `./**/build/reports/tests/**` 업로드 추가.
    - 실제 병렬 효과는 첫 실행 이후 캐시가 데워진 다음에 관측 가능.

### [x] 1-5. Secrets 주입 방식 개선

- 현재: heredoc 방식. multi-line JSON 에서 변수 expansion / escape 취약점.
- 할 일: base64 인코딩 → `base64 -d` 디코딩 방식 또는 안전한 write 방식으로 치환.
- 관련: GitHub Actions 의 secret masking 과도 어울려야 함.
- 결정/결과: **1-2 에 흡수되어 해소**. composite action 내부에서 `env:` 로 secret 을 주입 후 `printf '%s' "$VAR" > path` 로 기록. base64 인코딩 전환(GitHub Secrets 재등록) 없이도 heredoc 의 expansion/escape 이슈는 제거됨. keystore 만 기존대로 base64 로 저장(바이너리 파일이라 텍스트 기록 불가).

---

## Phase 2. 코드 품질 강화

### [x] 2-1. Android Lint CI 통합

- 현재: ktlint 만. Android Lint 는 돌지 않음.
- 할 일: `./gradlew lintStagingDebug` 추가. `app/lint-baseline.xml` 도입하여 기존 경고는 baseline 처리.
- 고려: 경고 양이 많으면 baseline 으로 처음엔 가드만 세우고 점진적으로 줄인다.
- 결정/결과:
    - 최초 실측: **86 errors / 254 warnings / 2 hints**. 일괄 수정은 이 PR 범위 밖.
    - `app/build.gradle.kts` 의 `android { ... }` 에 `lint { baseline = file("lint-baseline.xml") }` 추가, `./gradlew updateLintBaseline` 으로 baseline 생성 (app/lint-baseline.xml, 약 3700 줄).
    - `ci.yml` 의 `build-and-test` job 이 `assembleStagingDebug testStagingDebugUnitTest lintStagingDebug` 를 한 명령으로 실행 — compile 공유.
    - 실패 시 lint html report 업로드.
    - **후속 과제**: baseline 을 점진적으로 줄이는 작업. 별도 이슈/PR 로 단계적으로 처리해야 함. 여기선 가드만 세움.

### [x] 2-2. detekt 도입 검토

- 현재: 없음.
- 할 일: 먼저 **검토**. 프로젝트 규모 대비 유용성 / ktlint 와의 역할 중복 판단. 도입 결정 시 규칙 세트 선정 후 별 PR.
- 고려: ktlint 는 포맷팅, detekt 는 코드 스멜 — 역할 다름. 둘 다 돌려도 의미 있음.
- 결정/결과: **현 시점 도입 보류**.
    - 근거:
        - ktlint (포맷팅) + Android Lint (Android/리소스/버그 패턴) 로 기본 가드 이미 확보 (2-1 도입).
        - 2-1 에서 경험했듯 기존 Kotlin 코드베이스는 정적 분석 baseline 규모가 크다(lint baseline 3700 줄). detekt 도입 시 또 하나의 baseline 을 관리해야 하며, 초기 규칙 튜닝 / 팀 합의 비용 존재.
        - 리팩토링이 활발한 시기에는 baseline 이 자주 움직여 가치가 부분적으로 상쇄.
    - 재도입 트리거:
        - 복잡도(Cyclomatic, LongMethod 등) 규칙을 팀이 적극적으로 원함.
        - custom 룰(도메인 패턴 강제) 필요.
        - Android Lint 커버하지 못하는 Kotlin 특유의 코드 스멜이 리뷰에서 반복적으로 지적됨.
    - 재도입 시 절차: rule set 선정 → `./gradlew detekt` baseline 생성 → CI 통합 → 팀에 공지.

---

## Phase 3. PR 자동화

### [ ] 3-1. PR labeler 도입

- 할 일: `actions/labeler` 로 변경된 파일 경로 기반 영역 라벨 (`feature/bookmark`, `core/network`, `ci`, `docs` 등) 자동 부여.
- 효과: PR 리뷰어/필터링 편의.

### [ ] 3-2. PR size label

- 할 일: `CodelyTV/pr-size-labeler` 또는 유사 액션으로 S/M/L/XL 라벨링.

### [ ] 3-3. Danger-Kotlin 검토

- 현재: 없음.
- 할 일: 먼저 **검토**. "체크리스트 미작성 / 큰 PR 경고 / CHANGELOG 업데이트 확인" 등 어떤 규칙을 강제할지 먼저 정의 → 규칙 없으면 도입 보류.

---

## Phase 4. 릴리스 자동화

### [ ] 4-1. `cd.yml` ↔ `manual_deploy.yml` 정리

- 현재: 두 워크플로우가 거의 같은 job 을 중복으로 들고 있음. (Phase 1-2 composite action 이 적용되면 부담이 크게 줄어듦)
- 할 일: reusable workflow (`workflow_call`) 로 공통화 검토.

### [ ] 4-2. `version.properties` 자동 bump

- 할 일: 릴리스 브랜치 생성 시 버전 자동 증분 워크플로우. semver 규칙 선정 필요.
- 고려: 현재 릴리스 플로우(누가, 언제 `version.properties` 를 바꾸는지)를 먼저 파악해야 안전.

### [ ] 4-3. 릴리스 노트 자동 생성

- 할 일: PR 제목/라벨 기반 `release-drafter` 도입 검토.
- 전제: 커밋/PR 메시지 컨벤션(현재 `[폴더위계정리] refactor: ...` 같은 대괄호 prefix)이 있어서 릴리스 노트 그룹핑 규칙을 어떻게 잡을지 결정 필요.

---

## Phase 5. 의존성 관리 자동화

### [ ] 5-1. Renovate vs Dependabot 선정

- 현재: 없음.
- 할 일: Android 생태계에서는 Renovate 가 Gradle version catalog 와의 호환성이 더 좋음. 먼저 **검토**, 정책(자동 머지 범위, PR 빈도) 결정 후 도입.

---

## Phase 6. 테스트 확장

### [ ] 6-1. Instrumented test (Firebase Test Lab) 도입 검토

- 현재: unit test 만.
- 할 일: 실제 디바이스 테스트 필요성 / 비용 / 유지보수 부담 먼저 평가.

### [ ] 6-2. Screenshot test 도입 검토

- 후보: Paparazzi (JVM 기반, instrumented 불필요).
- 할 일: Compose 전환 정도 대비 ROI 평가.

---

## Phase 7. 관측 / 성능

### [ ] 7-1. Gradle Build scan / Develocity 검토

- 할 일: OSS build-scan 무료 티어로 먼저 실험 → 빌드 속도 병목 분석 근거 마련.

---

## 진행 규칙

- 각 항목 완료 시 체크박스 갱신 + 커밋 메시지에 항목 번호 포함 (예: `ci: [1-2] secrets setup composite action 추출`).
- Phase 내부라도 항목끼리 독립성이 있으면 별도 PR. 서로 영향 주는 것(예: 1-2 와 1-4)은 묶어서 가능.
- 검토 항목(2-2, 3-3, 4-2, 5-1, 6-1, 6-2, 7-1)은 결론이 "도입 보류"라도 결론/근거를 이 문서에 남긴다.
