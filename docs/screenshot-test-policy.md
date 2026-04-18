# Compose Screenshot Test 정책

이 문서는 SNUTT Android의 Compose Preview Screenshot Test 정책을 기술한다. 꼼꼼하고, 코드 변경에 따라 자연스럽게
유지보수되는 시각 회귀 방어막을 만드는 것이 목적이다.

---

## 1. 도구 선택

- Jetpack Compose 공식 **Compose Preview Screenshot Testing** (`com.android.compose.screenshot` Gradle
  플러그인)을 사용한다.
- 이 도구는 2026-04 시점에 아직 alpha 상태이다. API 변경 및 AGP 호환성 이슈 가능성을 감수하고 채택한다.
- 채택 이유: (a) 공식/네이티브 지원, (b) `@Preview` 를 그대로 테스트 입력으로 사용 — 기존 컴포저블 작성 관습과 충돌하지 않음.
- 대안(Paparazzi, Roborazzi)은 현재 후보에서 제외한다. 공식 도구가 안정화되기 전까지 재검토 가능.

---

## 2. `main` 과 `screenshotTest` 의 역할 구분

프로젝트에는 두 종류의 `@Preview` 가 공존한다. **역할이 명백히 다르므로 소스셋으로 분리한다.**

| 축       | `main` 의 `@Preview`                                         | `screenshotTest` 의 `@Preview`                                   |
|---------|-------------------------------------------------------------|-----------------------------------------------------------------|
| 목적      | 개발자의 **시각적 단서**                                             | 코드 변경에 따른 **의도치 않은 시각 회귀 방어**                                   |
| 커버리지 기준 | 해당 컴포넌트가 "어떻게 생겼는지" 개발자가 인지할 수 있을 정도                        | **유의미한 모든 분기** (§3)                                             |
| 예시      | `LectureActionButtons`: 버튼 3종(강의평/삭제/초기화) 생김새만 확인되면 충분 → 3장 | 모든 `lecture × editMode × hideDeleteButton` 의미 분기를 각 1장씩 → 5장 이상 |
| 변경 시 영향 | 개발자가 자유롭게 수정/추가/삭제 가능                                       | 수정은 **의도된 UI 변경 시에만** — diff가 나면 골든 이미지 갱신(record) 필요           |
| 파일 위치   | 컴포저블과 동일 파일                                                 | `app/src/screenshotTest/...` 대응 경로                              |

**도구 제약**: `com.android.compose.screenshot` 플러그인은 `screenshotTest` 소스셋의 `@Preview` 만 스캔한다. main의
`@Preview` 는 테스트 대상이 아니다. 위 분리는 철학과 기술 제약이 일치하는 지점이다.

---

## 3. 커버리지 원칙

### 3.1 단위

- **컴포넌트 단위** 로 커버한다. 플로우/Screen 단위 테스트는 현재 범위에서 제외한다 (향후 재논의 §7).
- "컴포넌트" 란 `feature/` 하위에서 `@Composable` 로 선언된 (보통 하나 이상의 파라미터를 받는) 재사용 가능한 UI 요소를 말한다.

### 3.2 "유의미한 분기"의 정의

커버리지 기준은 **파라미터의 곱집합이 아니라, 실제 `when`/`if` 분기 로직이 바뀌는 경로** 이다.

예: `LectureActionButtons` 의 의미 분기 도출

| 분기 결정 로직                                                        | 분기 수 |
|-----------------------------------------------------------------|------|
| `lecture is LectureSyllabusInfo && !editMode` (강의평/강의계획서 표시 여부) | 2    |
| `lecture` 타입 (`SyllabusLecture` / `CustomLecture` / 그 외)        | 3    |
| `editMode` (`SyllabusLecture` 에서 버튼 레이블/핸들러 분기)                 | 2    |
| `hideDeleteButton` (전체 삭제/초기화 블록 노출 여부)                         | 2    |

파라미터 공간 전체를 곱하면 24가지이지만, 실제로 **시각적 차이가 발생하는 조합**은 다음과 같다 (타입 계층:
`SyllabusLecture : LectureSyllabusInfo`, `CustomLecture` — `LectureSyllabusInfo` 미구현,
`SearchedLecture : LectureSyllabusInfo`):

1. `SyllabusLecture` + `editMode=false` → 상단 2개(강의계획서/강의평) + 삭제
2. `SyllabusLecture` + `editMode=true` → 초기화만
3. `CustomLecture` + `editMode=false` → 삭제만 (`CustomLecture`는 `LectureSyllabusInfo` 미구현 → 상단 블록 렌더 안
   됨)
4. `CustomLecture` + `editMode=true` → 빈 화면 (`AnimatedVisibility` 로 삭제도 숨김)
5. `SearchedLecture` + `editMode=false` → 상단 2개만 (`when (lecture)` 의 `else` 분기 → 하단 없음)
6. `hideDeleteButton=true` + `SyllabusLecture` + `editMode=false` → 상단 2개만

**이 수준으로 분기를 도출한 뒤 하나씩 preview 를 작성한다.** 분기 도출과 preview 동기화는 `screenshot-sync` Skill 이 수행한다 (§6 참조).
이 문서는 의사결정 근거와 정책을 정의하고, 실행 절차는 Skill 에서 세분화한다.

### 3.3 분기가 아닌 파라미터 변동

텍스트 길이, 색상 토큰 등 "분기 로직에 개입하지 않는 단순 파라미터 변동"은 커버 대상이 아니다. 테스트 preview 에 포함하지 않는다.
(예: `LectureActionButton` 의 `title` 파라미터 문자열이 길어졌을 때 레이아웃이 깨지는지 — 이건 별도 관심사이며, 필요하면 전용
preview 를 추가한다.)

### 3.4 멀티 컨피그

- `@PreviewLightDark`, `@PreviewFontScale`, `@PreviewScreenSizes` 는 **기본적으로 적용하지 않는다.** 테스트 수가
  N배로 불어 나 노이즈와 유지보수 부담이 커버리지 가치를 상회할 수 있다.
- 다크모드/폰트스케일 등에서 실제 회귀가 잦은 핵심 컴포넌트에 한해 선별 적용한다.

---

## 4. 파일 / 네이밍 규약

- 컴포저블 파일과 테스트 파일은 **1:1 대응** 한다.

  | 컴포저블 | 테스트 |
      |---|---|
  | `app/src/main/.../feature/lecturedetail/LectureActionButtons.kt` | `app/src/screenshotTest/.../feature/lecturedetail/LectureActionButtonsScreenshotTest.kt` |

- 테스트 파일 내 preview 함수 이름: `{ComponentName}_{분기식별자}`

  ```kotlin
  @Preview
  @Composable
  private fun LectureActionButtons_SyllabusLecture_ViewMode() { ... }

  @Preview
  @Composable
  private fun LectureActionButtons_SyllabusLecture_EditMode() { ... }

  @Preview
  @Composable
  private fun LectureActionButtons_CustomLecture_ViewMode() { ... }

  @Preview
  @Composable
  private fun LectureActionButtons_HideDeleteButton() { ... }
  ```

    - 분기 식별자는 **의미 단위** 로 명명한다. 파라미터 나열(`editMode=true_hideDeleteButton=false`) 식의 기계적 이름은 피한다.
    - 한 테스트 파일 안의 preview 이름은 전부 `{ComponentName}_` 접두어를 통일한다. 생성되는 골든 이미지 파일 정렬에 이점이 있다.

- `PreviewData` (`com.wafflestudio.snutt2.domain.model.preview.PreviewData`) 는 main/screenshotTest
  양쪽에서
  공유한다. 테스트 전용 preview fixture 가 필요해지면 `PreviewData` 를 확장하는 방향으로 둔다 — test-only fixture 를
  `screenshotTest/` 에 따로 두지 않는다.

---

## 5. "인라인해야 한다"의 휴리스틱

별도 컴포저블로 분리되어 있는데 스크린샷 테스트 대상으로 삼을 가치가 없어 보인다면, 인라인의 코드 스멜일 수 있다. 판단은 다음 두 조건이
**모두** 성립할 때만 한다.

1. 스킵 경계(recompose 최적화), 접근성, 테마/스타일 합성 등 **독립된 컴포저블로 유지해야 할 정당한 사유가 없다.**
2. 유의미한 시각 분기가 **0 또는 1개** 이다.

두 조건을 모두 충족하면 인라인 후보로 보고, 리팩토링을 검토한다. 어느 한쪽이라도 충족되지 않으면 유지한다.

`compose-stability-policy.md` 와의 상호작용에 유의: 스킵 경계 목적으로 작게 쪼갠 컴포저블을 "테스트할 게 없다" 는 이유로 인라인하면
recompose 성능 회귀가 발생할 수 있다.

---

## 6. 역할 분담

테스트 인프라의 유지보수 주체를 셋으로 나눈다: (a) Gradle 플러그인, (b) `screenshot-sync` Skill (AI agent), (c) 사람.

### 6.1 Gradle 플러그인이 담당

`com.android.compose.screenshot` 플러그인은 `screenshotTest/` 소스셋의 `@Preview` 를 자동으로 스캔하고 실행한다.

- 새 `@Preview` 함수 → 테스트 케이스로 자동 편입 (별도 등록 코드 불필요).
- 컴포저블 시그니처가 바뀌면 preview 호출부도 컴파일 대상이므로, **이름·타입 변경은 컴파일 에러로 강제로 드러난다.**
- 골든 이미지 비교 및 diff 리포트 생성.

이 층은 결정론적이며 수정 대상이 아니다.

### 6.2 `screenshot-sync` Skill 이 담당

preview 코드 (`<ComponentName>ScreenshotTest.kt`) 의 의미 분기 ↔ 구현 동기화는
`.claude/skills/screenshot-sync/SKILL.md` 로 정의된 AI agent 절차가 담당한다.

- 대상 컴포저블의 의미 분기 도출 (§3.2 기준).
- 신규 작성 / 추가 / 업데이트 / 삭제 / 리네이밍.
- 자체 검증 (시그니처 일치, 분기 수 대조, fixture 존재 확인, 인라인 휴리스틱).

Skill 의 **호출 시점/주체는 이 정책이 규정하지 않는다.** 사람이 대화 중 직접 호출, PR 리뷰 훅, push 이벤트 등 운영 방침에 따라 자유롭게 결정할 수 있다.
Skill 자체는 호출 경로와 무관하게 동일 절차를 수행한다.

이 층은 확률적 추론에 기반한다. 기계적 정적 분석(KSP 등)보다 탐지력이 낮을 수 있다는 리스크는 감수한다 — AI agent 의 추론 능력 발전에 기대하며, 기계적 탐지보다
유연하고 값싸다는 판단이다.

### 6.3 사람이 담당

사람에게 남는 역할은 **의도 판단** 하나이다.

- **골든 이미지 diff 의 의도/회귀 판단.** 시각 출력이 바뀌었을 때, 이 변경이 의도된 UI 변경인지 회귀인지는 기계도 agent 도 판단할 수 없다. diff 리포트를
  보고 사람이 판단한 뒤 `./gradlew :app:recordStagingDebugScreenshotTest` 로 골든을 갱신한다.

이 외의 반복 작업 (preview 열거, 시그니처 맞추기 등) 은 사람이 직접 하지 않는다.

### 6.4 CI 통합 (TBD)

- `validateStagingDebugScreenshotTest` 를 PR 체크에 편입하는 시점/조건은 POC 이후 별도 논의.
- 초기에는 로컬 실행만 하고, 골든 이미지 안정성이 확보된 후 CI 도입을 판단한다.
- Skill 자동 호출(예: PR 리뷰 시점 훅) 은 §6.2 의 "호출 시점 open" 원칙 하에 별도 결정 사항으로 둔다.

---

## 7. 범위 및 향후 검토

### 7.1 이번 POC 범위

- 첫 타겟: `feature/lecturedetail/`
- 산출물: Gradle 설정 + `LectureActionButtons` 테스트용 preview 세트 + `./gradlew validateDebugScreenshotTest`
  최초 통과
- POC 완료 후 이 문서의 규약을 재검토하고, 필요하면 개정한 뒤 다른 feature 로 확장한다.

### 7.2 향후 검토 항목

- **Screen/플로우 단위 테스트 확장**: 컴포넌트 단위는 UI 회귀(레이아웃, 스타일)는 잡지만, UiState 가 Screen 으로 조립되는 과정의
  회귀(예: Loading + Dialog 중첩 상태)는 잡지 못한다. 필요성이 확인되면 별도 계층으로 도입한다.
- **멀티 컨피그 확장**: 다크모드/폰트스케일 회귀가 실제 문제가 되는 시점에 선별 컴포넌트부터 적용.
- **Skill 호출 자동화**: `screenshot-sync` 를 PR 리뷰 훅, push 이벤트 등에서 자동 호출하는 운영 방식. 현재는 수동 호출만 상정한다.

---

## 8. 요약 체크리스트

새 컴포넌트를 작성하거나 기존 컴포넌트를 변경할 때:

- [ ] main `@Preview` 는 시각적 단서로서 충분한가? (분기 커버리지가 아니라 "대표 생김새")
- [ ] `screenshotTest/` 에 대응 파일(`{ComponentName}ScreenshotTest.kt`) 이 있는가?
- [ ] 컴포넌트의 **유의미한 시각 분기** 를 모두 식별했는가? (파라미터 곱집합이 아님)
- [ ] 각 분기 당 preview 1개, `{ComponentName}_{분기식별자}` 네이밍을 지켰는가?
- [ ] diff 가 발생했다면 의도된 변경인지 확인하고 골든 이미지를 갱신했는가?
- [ ] (인라인 휴리스틱) 분리된 컴포저블인데 분기 0~1개이고 정당 사유도 없다면 인라인을 검토했는가?
