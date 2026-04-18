---
name: screenshot-sync
description: >
  Compose Preview Screenshot Test의 테스트용 `@Preview`를 대상 컴포저블과 동기화하는 스킬.
  (1) 작성 모드 — 대상 컴포저블의 screenshotTest가 아직 없을 때 신규로 작성한다.
  (2) 동기화 모드 — 기존 screenshotTest가 있는 컴포저블이 변경되었을 때 의미 분기를 재도출하고 preview를 추가/변경/삭제한다.
  "스크린샷 테스트 작성", "screenshotTest 동기화", "preview 테스트 맞춰줘",
  "스크린샷 분기 추가", "compose screenshot", "이 컴포저블 screenshotTest" 등의 요청 시 사용한다.
  정책 및 ADR: `docs/screenshot-test-policy.md`.
---

# Compose Screenshot Test 동기화

이 스킬은 `feature/` 하위의 컴포저블 하나를 입력으로 받아, 대응하는
`app/src/screenshotTest/.../<ComponentName>ScreenshotTest.kt` 를 동기화한다.

호출 시점은 이 스킬이 규정하지 않는다. 사람이 직접 대화 중 호출할 수도 있고, PR 리뷰 시 점검용으로
호출할 수도 있고, develop push 이벤트 등에 의해 자동 트리거될 수도 있다. 어떤 경로로 호출되든 스킬 자체의
절차는 동일하다.

| 사용자 요청 | 모드 |
|---|---|
| "이 컴포저블 screenshotTest 작성해줘" (대응 파일 없음) | **작성 모드** |
| "컴포저블 바꿨으니 screenshotTest 업데이트해줘" (대응 파일 있음) | **동기화 모드** |
| 입력만 주고 모드 미지정 | 대응 파일 존재 여부로 자동 결정 |

**두 모드 모두 "공통: 분기 분석" 섹션부터 시작한다.**

---

# 공통: 분기 분석

## 1. 대상 컴포저블 소스 읽기

대상 `@Composable` 함수를 읽고 다음을 파악한다.

| 항목 | 파악 내용 |
|------|-----------|
| 함수 시그니처 | 파라미터 이름·타입·기본값 |
| 분기 원인 | `when`, `if`, `AnimatedVisibility(visible = ...)`, `?.let`, null 체크, `when`의 `else` 누락 여부 |
| 내부 컴포저블 | 하위 `@Composable` 호출 중 분기에 따라 렌더되거나 안 되는 것 |
| 타입 계층 | 파라미터가 sealed class/interface 라면 실제 서브타입 전체 (코드 전역에서 탐색) |

`when (x)` 에 `else -> {}` 가 있다면 이는 **누락 분기가 아니라 명시적 무시**이다. 이 경로도 의미 분기의
하나로 취급한다(아무것도 안 보이는 시각 결과).

## 2. 의미 분기 도출

**정의**: 의미 분기란 동일 컴포저블을 다른 파라미터로 호출했을 때 **시각 출력이 달라지는** 경로이다.

- 파라미터 공간의 곱집합이 **아니다.**
- 분기 로직에 개입하지 않는 단순 변동(텍스트 길이, 색상 토큰 등)은 의미 분기가 **아니다.**
- 같은 시각 결과를 내는 파라미터 조합은 **하나로 합친다.**

도출 절차:

1. 코드 안의 모든 분기 원인을 나열한다.
2. 각 분기 원인이 만드는 **실제 시각 차이** 를 판단한다. `AnimatedVisibility(visible = X)` 는 X에 따라 렌더/미렌더.
3. 조합을 열거하되, **시각적으로 같은 결과** 인 조합은 하나로 합친다.
4. 남은 고유한 시각 출력 집합이 의미 분기 목록이다.

### 예시: `LectureActionButtons`

분기 원인:

- `lecture is LectureSyllabusInfo && !editMode` → 상단 블록(강의계획서/강의평) 가시성
- `when (lecture)` → 하단 블록 분기
    - `CustomLecture` → `AnimatedVisibility(visible = !editMode) { 삭제 }`
    - `SyllabusLecture` → `if (editMode) 초기화 else 삭제`
    - `else` (`SearchedLecture`) → 아무것도 없음
- `hideDeleteButton` → 하단 블록 전체를 건너뜀

타입 계층 (전역 탐색):

- `SyllabusLecture : LocalLecture, LectureSyllabusInfo`
- `CustomLecture : LocalLecture` — `LectureSyllabusInfo` 아님 → 상단 블록 미렌더
- `SearchedLecture : Lecture, LectureSyllabusInfo` — `when (lecture)` 의 `else` → 하단 블록 미렌더

의미 분기:

1. SyllabusLecture + 보기 모드 → 상단 2개 + 삭제
2. SyllabusLecture + 편집 모드 → 초기화만
3. CustomLecture + 보기 모드 → 삭제만
4. CustomLecture + 편집 모드 → 빈 화면 (AnimatedVisibility 로 삭제도 숨김)
5. SearchedLecture + 보기 모드 → 상단 2개만 (else → 하단 없음)
6. `hideDeleteButton = true` + SyllabusLecture + 보기 모드 → 상단 2개만

## 3. Fixture 결정

각 분기에 필요한 도메인 객체는 `com.wafflestudio.snutt2.domain.model.preview.PreviewData` 에서 고른다.
**main/screenshotTest 양쪽에서 공유** 하므로 test-only fixture 를 `screenshotTest/` 에 따로 두지 않는다.
필요한 fixture 가 없으면 `PreviewData` 에 추가한다.

## 4. 인라인 휴리스틱 체크

도출된 의미 분기가 **0 또는 1개** 이고, 동시에 **독립 유지의 정당한 사유가 없다면** 해당 컴포저블은 인라인
후보이다. 두 조건이 모두 충족될 때만 경고한다.

"독립 유지의 정당한 사유" 는 아래 중 하나라도 해당하면 성립한다 (비망라):

- **스킵 경계(recompose 최적화)** — `docs/compose-stability-policy.md` 에 따른 분리. 인라인하면 재계산
  범위가 넓어져 성능 회귀가 발생할 수 있다.
- **접근성·테마/스타일 합성** — `CompositionLocalProvider`, 접근성 속성 주입 등 호출부가 신경 쓰지 않아야
  하는 cross-cutting concern.
- **외부 host 의 content slot** — `ModalBottomSheet.sheetContent`, `AlertDialog` content, `Scaffold`
  slot 등 "호출부와 독립적으로 재사용/전달되는 한 덩어리" 로 존재하는 것이 자연스러운 영역.
- **내부 상태(`remember`)를 소유한 자기완결 편집/입력 플로우** — 시작 상태를 외부에서 받아 확정 시점에만
  결과를 올려 보내는 편집 다이얼로그/시트 내용물, form 섹션 등. 호출부 입장에서 한 덩어리 UX 로 인식되므로
  인라인 대상이 아니다.
- **호출부가 한 덩어리로 인식하는 섹션** — 네비게이션 헤더, 폼 섹션, 리스트 아이템 등 이름이 곧 의도를
  전달하는 경계. 분기 수와 무관하게 가독성·재사용성 측면에서 유지 가치가 있다.

위 사유가 하나라도 해당하면 유지 판단이다. 어디에도 해당하지 않고 분기가 0~1개일 때만 결과 보고에
"인라인을 검토하라" 는 경고를 포함한다.

---

# 작성 모드 (공통 분기 분석 이후)

## 1. 테스트 파일 경로 결정

```
app/src/main/<패키지경로>/<ComponentName>.kt
→ app/src/screenshotTest/<패키지경로>/<ComponentName>ScreenshotTest.kt
```

패키지 경로와 패키지 선언은 main 과 동일하게 유지한다 (`internal` 가시성 컴포저블도 동일 패키지에서 접근 가능).

## 2. 파일 골격

```kotlin
package com.wafflestudio.snutt2.feature.<path>

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.wafflestudio.snutt2.domain.model.preview.PreviewData

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun <ComponentName>_<분기식별자1>() {
    <ComponentName>(
        // 의미 분기 1에 해당하는 파라미터
    )
}

@PreviewTest
@Preview(showBackground = true, widthDp = 360, locale = "ko")
@Composable
fun <ComponentName>_<분기식별자2>() { ... }
```

- 모든 preview 함수는 `@PreviewTest` 로 표시하고 **public** 으로 둔다. alpha14 는 이 어노테이션이 붙은
  top-level public 함수만 preview 로 discover 한다. `private` 로 두면 "discovered no tests" 로 실행 단계에서
  조용히 누락된다.
- 람다 파라미터는 `{}` 로 무력화한다. 실제 동작은 screenshot test 의 관심사가 아니다.
- `@Preview` 옵션: 기본 `showBackground = true`, `widthDp = 360`, `locale = "ko"`. locale 은 prod 주
  사용 언어에 맞춰 고정한다. 다국어 회귀를 별도로 방어할 필요가 생긴 컴포넌트에 한해 개별 preview 에서
  locale 을 오버라이드한다.
- 멀티 컨피그 (`@PreviewLightDark`, `@PreviewFontScale`, `@PreviewScreenSizes`) 는 기본 **적용하지 않는다.**
  다크모드/폰트스케일 등에서 회귀가 잦은 것이 확인된 핵심 컴포넌트에만 선별 적용한다.

## 3. 네이밍 규약

- 함수 이름: `<ComponentName>_<분기식별자>`
- 분기 식별자는 **의미 단위** (예: `SyllabusLecture_ViewMode`, `HideDeleteButton`).
- 파라미터 나열식 (`editMode=true_hideDeleteButton=false`) 금지.
- 동일 컴포넌트의 모든 preview 는 `<ComponentName>_` 접두어를 공유한다.

---

# 동기화 모드 (공통 분기 분석 이후)

## 1. 기존 preview 와 대조

기존 `<ComponentName>ScreenshotTest.kt` 를 읽고 도출한 의미 분기 목록과 대조한다.

| 의미 분기 | 기존 preview | 시그니처 | 처리 |
|---|---|---|---|
| O | O | 일치 | **유지** |
| O | O | 불일치 | **업데이트** — 현재 시그니처에 맞게 호출부 수정 |
| O | X | — | **추가** — 새 preview 작성 |
| X | O | — | **삭제** — 기존 preview 제거 |

## 2. 시그니처 변화 처리

대상 컴포저블의 파라미터가 추가/제거/개명되면 모든 preview 의 호출부를 동기화한다. 기본값이 있는 파라미터가
추가됐다면 기존 preview 는 호출을 바꾸지 않아도 빌드는 통과하지만, **의미 분기에 새 파라미터가 참여한다면**
대응 preview 를 추가해야 한다 (§1 의 "추가" 케이스).

## 3. 리네이밍 처리

대상 컴포저블 이름이 바뀌었다면 파일명과 모든 preview 함수의 접두어를 새 이름으로 갱신한다.

## 4. 삭제 결정에 신중

"의미 분기가 사라졌다"는 판단은 분기 원인 (when/if 등) 자체가 제거·변경됐을 때만 한다. 단순 리팩토링(변수
이름 변경, 내부 로직 정리 등) 으로는 삭제하지 않는다. 애매하면 유지 쪽으로 기운다.

## 5. 중복 감지

다른 이름이지만 같은 의미 분기를 커버하는 preview 가 발견되면 하나만 남긴다. 네이밍이 불일치하는 쪽을
§3 의 분기 식별자 규약에 맞게 교정한다.

---

# 작성/동기화 완료 후 자체 검증

스킬 실행 직후 다음을 확인한다.

1. **시그니처 재확인** — 작성한 파일을 다시 읽어서 모든 preview 호출이 대상 컴포저블의 현재 시그니처와
   일치하는지 확인. 컴파일 에러 가능성 차단.
2. **분기 수 대조** — preview 수 == 도출된 의미 분기 수. 불일치 시 누락 원인을 파악하고 재시도.
3. **Fixture 존재 확인** — `PreviewData.<name>` 이 실제로 존재하고 import 경로가 정확한지 확인.
4. **인라인 휴리스틱 재확인** — 분기 ≤ 1 + 정당 사유 없음 → 인라인 경고 포함.

빌드 검증은 필요 시:

```bash
# 최초 골든 생성 / 의도된 diff 반영
./gradlew :app:updateStagingDebugScreenshotTest
# 이후 검증
./gradlew :app:validateStagingDebugScreenshotTest
```

alpha14 에서는 골든 갱신 태스크 이름이 `update*` 이다 (구버전의 `record*` 아님).

**골든 파일명에 preview config hash 가 들어간다.** `@Preview` 의 옵션(`locale`, `widthDp` 등) 이 변경되면
파일명 hash 가 바뀌어 이전 골든이 고아(orphan) 가 된다. `update*` 는 새 골든을 추가할 뿐 이전 파일을 지우지
않으므로, 옵션을 바꾼 뒤에는 `find app/src/screenshotTestStagingDebug/reference -name "*_<이전hash>_0.png"
-delete` 로 수동 정리한다. 컴포저블 이름·함수명 변경 시에도 동일.

이 스킬은 기본적으로 빌드까지 돌리지 않는다 (변경 단위가 작을 때 과한 비용). 사용자가 명시적으로 요청하거나
변경 범위가 클 때 실행한다.

### 환경 주의사항 — Kotlin 2.3.20 × alpha14 workaround

현재 조합에서는 `app/build.gradle.kts` 에 `screenshotTest` configuration 의 `kotlin-reflect` 를
2.3.0 으로 force 하는 블록이 없으면 layoutlib 렌더에서 `ClassNotFoundException` 으로 전 preview 가 실패한다.
원인·제거 조건은 `docs/screenshot-test-policy.md` §1.1. 빌드가 해당 예외로 실패하면 이 블록이 유지되고
있는지부터 확인한다.

---

# 보고 형식

작업 완료 후 사용자에게 다음을 보고한다.

- 처리한 파일 경로 (컴포저블, 테스트)
- 도출된 의미 분기 개수와 목록 (한 줄씩, 각 분기가 어떤 시각 결과를 내는지 명시)
- 추가/변경/삭제한 preview 항목
- 인라인 휴리스틱 경고 (해당 시)
- 다음 권고 액션: diff 발생이 예상되면 `./gradlew :app:validateStagingDebugScreenshotTest` 실행 후
  `updateStagingDebugScreenshotTest` 로 골든 갱신 필요. 골든 변경이 의도된 UI 변경인지는 사람이 판단.

---

# 이 스킬이 다루지 않는 것

- **골든 이미지 갱신의 의도/회귀 판단** — 시각 diff 를 보고 판단하는 건 사람의 영역이다. 스킬은 preview 코드
  동기화까지만 책임진다.
- **Screen/플로우 단위 테스트** — 현재 범위는 컴포넌트 단위이다. Screen 단위는 정책 문서 §7.2 의 향후 검토
  대상이며 이 스킬에서 다루지 않는다.
- **멀티 컨피그 (다크모드, 폰트스케일) 전면 적용** — 선별 적용은 사용자가 요청 시에만. 기본은 단일 컨피그.
- **자동 트리거 설정** — 이 스킬은 호출되었을 때 해야 할 일을 규정할 뿐, 언제 호출할지는 팀의 운영 방침에
  따른다.