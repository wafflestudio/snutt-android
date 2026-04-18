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

## Phase 3. ~~PR 자동화~~ (삭제)

사용자 판단으로 제거. PR labeler / size label / Danger-Kotlin 모두 현 팀 규모와 프로세스 특성상 ROI 불명확하여 도입하지 않음. 필요해지면 Phase 번호와 무관하게 별도 도입 검토.

---

## Phase 4. 릴리스 자동화

### 배경: 현재 릴리스 흐름 (수동)

1. 담당자가 `develop` 에서 `release-X.Y.Z` 브랜치 분기.
2. `version.properties` 를 `X.Y.Z-rc.1` 로 수정 → push.
3. `cd.yml` 트리거 → live AAB(Firebase) + staging APK(Firebase + Slack) 빌드/배포.
4. **(수동)** 담당자가 Play Console 에서 AAB 를 internal track 에 업로드 → 내부자 테스트 배포.
5. QA 에서 이슈 발견 시 release 브랜치에 수정 커밋 → **담당자가 직접** `version.properties` 를 `X.Y.Z-rc.2` 로 수정 push → 3–4 반복.
6. QA 완료 시 `version.properties` 를 `X.Y.Z` (rc 제거) 로 수정 push → live AAB 재빌드.
7. **(수동)** Play Console 에서 production 업로드 & 릴리스.
8. **(수동)** GitHub 에서 tag + release note 작성.
9. `release-X.Y.Z` 를 `develop` 으로 merge back.

### 자동화 목표 (Phase 4 완료 후)

1. 담당자가 `release-X.Y.Z` 브랜치 분기. (유지, 시작 시그널)
2. 담당자가 `version.properties` 를 `X.Y.Z-rc.1` 로 최초 설정 push. (유지, 릴리스 시작 시그널)
3. **이후 release 브랜치에 코드 커밋 push 시 자동으로 `rc.N` → `rc.N+1` bump + 빌드 + Play Store internal 업로드 + Firebase + Slack.**
4. QA 완료 후 담당자가 `version.properties` 를 rc 제거해 push → **자동으로 live 빌드 + Play Store production `draft` 업로드 + GitHub Release draft/tag 생성 + Firebase + Slack**.
5. 담당자가 Play Console 에서 production release "Review and release" 클릭 (수동 유지 — blast radius 가드).
6. 담당자가 GitHub Release 페이지에서 **한국어 릴리스 노트 작성 후 publish** (수동 유지 — 릴리스마다 직접 작성).
7. `release-X.Y.Z` → `develop` merge back. (수동 유지)

### 트랙 정책 (결정됨)

- `-rc.N` 포함 버전 → `internal` track
- 정식 버전 → `production` track, **`status: draft`**. Play Console 에서 release 개시만 수동 클릭. `production` 에 완전 자동 승격은 blast radius 가 너무 큼. (옵션 (a) 자동 승격, (b) internal 만 + 수동 승격, (c) production draft, (d) 단계적 rollout 중 (c) 채택)

### 툴 선정: Fastlane (결정됨)

- 비교군: (A) r0adkll/upload-google-play GHA, (B) Fastlane supply, (C) Triple-T/gradle-play-publisher, (D) 공식 SDK 직접 호출, (E) curl + JWT.
- 최종 선택: **(B) Fastlane**.
    - Google 공식은 아니지만 Android 릴리스 자동화의 de facto standard. 유지보수 주체가 크고 오래됨.
    - CI 밖(로컬)에서도 동일 명령으로 재현 가능. 트러블슈팅 정보 풍부.
    - 향후 확장(스크린샷/메타데이터 자동화) 용이.
- (A) 는 단일 개인 메인테이너 의존. (C) 는 Gradle 설정이 과하게 커짐. (D)(E) 는 유지보수 부담.

### [ ] 4-1. Play Store 자동 업로드 (Fastlane)

- 목표: `cd.yml` 에서 AAB 빌드 후 Play Store 에 자동 업로드.
- 구성:
    - `Gemfile` 에 `fastlane` gem 추가.
    - `fastlane/Appfile`: `package_name` (live/staging 모두 `com.wafflestudio.snutt2.live` 등 variant suffix 포함), `json_key_file` 은 env 로 주입.
    - `fastlane/Fastfile` 에 2 개 레인 정의:
        - `upload_internal`: `aab` + `track: internal` + `release_status: completed` 로 업로드.
        - `upload_production_draft`: `aab` + `track: production` + `release_status: draft` 로 업로드.
    - CI 에서 `ruby/setup-ruby@v1` + `bundle install` → 트랙 판별 후 `bundle exec fastlane <lane>`.
- 트랙 판별 로직: `version.properties` 읽어 `snuttVersionName` 에 `-rc` 포함 여부로 분기. Shell step 에서 판별 후 `GITHUB_ENV` 에 `FASTLANE_LANE` 내보내거나, Fastfile 내부에서 판단.
- 필요한 사용자 사전 준비 (코드 변경으로 해결 불가):
    - Google Play Console → Settings → API access → 서비스 계정 발급 또는 기존 연결 확인.
    - 계정 권한: Release 를 만들/업로드할 수 있는 수준 ("Release manager" 이상).
    - 해당 서비스 계정 **JSON key 파일 다운로드**.
    - GitHub Secrets 에 `GOOGLE_PLAY_SERVICE_ACCOUNT` 이름으로 JSON **원문** 등록.
    - Play Console 에 **이미 최초 릴리스(internal)가 manual 로 한 번은 올라가 있어야** Fastlane 이후 업로드가 동작. (Play 정책)
- 개방된 설계 결정:
    - Fastlane metadata 관리(스크린샷/설명 텍스트 자동화)는 4-1 범위 **밖**. 일단 바이너리 업로드만.
    - staging variant 도 Play 에 올릴지? 현재 staging 은 applicationIdSuffix `.staging` 이라 별 package. **업로드 대상은 live 만** 으로 한정. staging 은 기존대로 Firebase + Slack.

### [ ] 4-2. reusable workflow 통합

- 현재: Phase 1-2 composite action 덕분에 secrets 중복은 해소. 그러나 Checkout / Setup Java / Setup Gradle / 빌드·Firebase·Slack 로직 자체는 `cd.yml` 과 `manual_deploy.yml` 에 여전히 중복.
- 4-1 Play Store 업로드까지 합치면 로직 분량이 더 커져 중복 비용이 크다.
- 설계:
    - `.github/workflows/_build-and-deploy.yml` (prefix `_` 는 "내부용" 관행) 을 `workflow_call` 로 정의.
    - inputs: `variant` (live/staging), `upload_firebase` (bool), `upload_play_store` (bool), `slack_message` (string, optional).
    - secrets: 필요한 모든 secret을 `secrets: inherit` 로 받음.
    - 책임: Checkout → Setup Java → Setup Gradle → Setup secrets(composite) → build → Firebase(조건부) → Play Store(조건부, 4-1 기준) → Slack(조건부).
- 호출 측:
    - `cd.yml`: `release-*` branch + `version.properties` 변경 push → job 2개 (live / staging) 가 각각 reusable workflow 호출.
    - `manual_deploy.yml`: `workflow_dispatch` → variant / firebase / play_store / slack 입력 받아 reusable workflow 호출.
    - `rc-bump.yml` (4-4): bump 후 내부적으로 reusable workflow 호출 (또는 commit push 로 cd.yml 간접 트리거).
- 리스크: reusable workflow 의 `secrets: inherit` 는 caller repo 와 callee 가 같은 repo 에 있을 때 잘 동작. 본 레포에서는 전부 내부 파일이므로 문제 없음.

### [ ] 4-3. GitHub Release draft 자동 생성

- 사용자 결정: PR 기반 자동 노트 생성 **안 함**. `release-drafter` 도입 안 함. 릴리스 노트는 매 릴리스마다 담당자가 한국어로 직접 작성.
- Phase 4 에서 자동화할 범위: **정식 릴리스 시점에 빈 draft Release + tag 를 자동 생성**. 담당자가 페이지 열어 본문만 작성 → publish.
- 구현:
    - `_build-and-deploy.yml` 또는 `cd.yml` 의 live 경로 말미에, `version.properties` 가 `-rc` 없는 정식 버전일 때만 실행되는 스텝 추가.
    - `softprops/action-gh-release@v2` 로 `tag_name: ${VERSION}`, `draft: true`, `name: v${VERSION}` Release 생성.
    - tag 도 같은 액션이 만들어줌 (`GITHUB_TOKEN` 권한: `contents: write` 필요).
- 수동 유지 영역: Release 본문(한국어), publish 버튼.
- 질문점(추후 결정): tag 기준 커밋을 어디로 할지. 현 흐름상 `release-X.Y.Z` 브랜치의 정식 bump 커밋이 기준이 되는 게 자연스러움. action-gh-release 는 기본적으로 트리거된 ref 기준으로 tag 를 만드므로 정합.

### [ ] 4-4. release 브랜치 push 기반 자동 rc bump

- 사용자 결정: **(iii) 완전 자동** 모드 채택. release 브랜치에 코드 커밋이 들어오면 자동으로 rc 가 +1 되고 빌드/배포까지 연쇄.
- 설계:
    - 새 워크플로우 `.github/workflows/rc-bump.yml`.
    - 트리거:
        ```yaml
        on:
          push:
            branches: ['release-*']
            paths-ignore: ['version.properties']
        ```
    - 즉 **`version.properties` 가 아닌 파일이 release 브랜치에 push 되면 트리거**. `version.properties` 를 바꾸는 push 는 기존 `cd.yml` 이 담당.
- 내부 동작:
    1. 현재 `version.properties` 읽어 `snuttVersionName` 파싱.
    2. 규칙:
        - 값이 `X.Y.Z` 형태(rc 없음)면: **에러로 중단**. 정식 버전 상태의 release 브랜치에 코드 커밋이 들어오는 건 의도 불명(이미 릴리스된 상태일 수 있음). 명시적 사용자 개입 필요.
        - 값이 `X.Y.Z-rc.N` 형태면: `X.Y.Z-rc.(N+1)` 로 계산.
    3. `version.properties` 업데이트 → commit(작자: `github-actions[bot]`) → push.
    4. 이 push 는 `version.properties` 변경을 포함하므로 `cd.yml` 이 자연 트리거되어 빌드/배포 수행.
- 무한 루프 방지:
    - `rc-bump.yml` 의 `paths-ignore: ['version.properties']` 덕분에 bump 커밋 자체는 `rc-bump.yml` 을 재트리거하지 않음.
    - 단, 담당자가 한 push 에 **코드 수정 + `version.properties` 를 함께 포함**하면 `paths-ignore` 가 skip 하지 않음 (GitHub Actions 의 paths-ignore semantics: 모든 변경 파일이 ignore 목록 안에 있을 때만 skip). 이 경우 이중 bump 가 일어날 수 있음.
    - 가드: `rc-bump.yml` 내부에서 "마지막 커밋이 이미 `version.properties` 를 변경했으면 skip" 검사 추가.
- 트리거 연쇄의 GITHUB_TOKEN 문제:
    - 일반 `GITHUB_TOKEN` 으로 만든 push 는 **다른 워크플로우를 트리거하지 않음**. 즉 `rc-bump.yml` 이 `GITHUB_TOKEN` 으로 push 하면 `cd.yml` 이 따라 돌지 않음.
    - 해결: `rc-bump.yml` 이 bump 후 **reusable workflow (`_build-and-deploy.yml`) 를 직접 호출**하여 빌드/배포 연쇄. 별도 workflow 간접 트리거 없이 한 워크플로우 내에서 모두 처리.
    - 이 설계는 `cd.yml` 의 기존 동작(버전 직접 편집 push)과도 호환됨 — `cd.yml` 역시 같은 reusable workflow 호출.
- Fastlane 트랙 로직과의 정합: bump 후 버전은 반드시 `-rc.N` 이므로 `upload_internal` 로만 감. 정식 릴리스는 4-4 대상이 아니고 담당자가 직접 `version.properties` 수정.

### 전체 흐름 정리 (Phase 4 완료 시)

```
[담당자] release-X.Y.Z 브랜치 분기
[담당자] version.properties → X.Y.Z-rc.1 push
  └─ cd.yml 트리거
       └─ _build-and-deploy.yml
            ├─ live AAB 빌드
            ├─ Firebase upload
            ├─ Fastlane: Play Store internal
            └─ Slack 알림

[담당자] 코드 수정 커밋 push (버그 수정 등)
  └─ rc-bump.yml 트리거 (paths-ignore: version.properties)
       ├─ rc.N → rc.N+1 계산
       ├─ version.properties commit + push
       └─ _build-and-deploy.yml 직접 호출
            ├─ live AAB 빌드
            ├─ Firebase upload
            ├─ Fastlane: Play Store internal
            └─ Slack 알림

[담당자] QA 완료 → version.properties → X.Y.Z (rc 제거) push
  └─ cd.yml 트리거
       └─ _build-and-deploy.yml
            ├─ live AAB 빌드
            ├─ Firebase upload
            ├─ Fastlane: Play Store production draft ★
            ├─ GitHub Release draft + tag 생성 ★
            └─ Slack 알림

[담당자] Play Console "Review and release" 수동 클릭
[담당자] GitHub Release 페이지에서 한국어 노트 작성 + publish
[담당자] release-X.Y.Z → develop merge back
```

### Phase 4 진행 순서

1. **4-2 reusable workflow 틀 먼저 구축**. 기존 `cd.yml`, `manual_deploy.yml` 의 현재 동작을 이 틀로 이관.
2. **4-1 Fastlane + Play Store 업로드** 를 reusable workflow 에 조건부 스텝으로 추가.
3. **4-3 GitHub Release draft** 생성 스텝 추가 (정식 버전일 때만).
4. **4-4 rc-bump 워크플로우** 신설 + reusable workflow 호출.

이 순서로 가는 이유: 4-2 가 나머지 전부의 기반이 되며, 4-4 는 reusable workflow 가 있어야 연쇄 호출이 깔끔. 4-1 은 Phase 4 의 핵심 가치지만 구조가 먼저 정돈된 후 붙이는 것이 리스크가 적음.

### Phase 4 사용자 사전 준비 체크리스트

- [ ] Google Play Console → Settings → API access → 서비스 계정 발급 또는 기존 서비스 계정 연결 확인
- [ ] 서비스 계정 권한 "Release manager" 이상 부여 (Releases 생성/업로드 필요)
- [ ] 서비스 계정 JSON key 파일 다운로드
- [ ] GitHub Secrets 에 `GOOGLE_PLAY_SERVICE_ACCOUNT` 이름으로 JSON 원문 등록
- [ ] Play Console 에 해당 `applicationId` 앱의 **최초 internal release 가 manual 로 한 번 업로드되어 있을 것** (Play Console 정책상 서비스 계정 업로드는 최초 수동 릴리스 이후에 가능)

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
