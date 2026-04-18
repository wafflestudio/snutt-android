# Compose Stability 정책

이 문서는 SNUTT Android의 Compose recompose 최적화 정책을 기술한다.

---

## 1. 기본 방침

- **기본형은 stdlib `List<T>`**. 선제적으로 `ImmutableList`/`PersistentList`로 전환하지 않는다.
- **실측으로 확인된 병목**에 한해, 원인을 분석한 뒤 최소 침습적으로 대응한다.
- 실측은 두 경로를 병행한다: (a) Layout Inspector — 유저 주도 발견 (b) recompose 카운터 테스트 — 가설 검증 및 회귀 방지.
- 추가로, **핵심 불변식을 지키는 최소한의 방어 테스트**를 상시 유지한다(§3.5). StrongSkipping 활성 상태·주요 타입의 equals 정합성을 자동으로 지켜 주는 정도에 그친다.

이 방침은 아래 배경에서 나온다.

---

## 2. 배경

### 2.1 Strong Skipping이 활성화되어 있다

프로젝트는 Compose Compiler의 Strong Skipping Mode를 기본 활성화 상태로 빌드된다
(`app/build/compose_compiler/stagingDebug/app-module.json`의 `strongSkipping: true`).

Strong Skipping 하에서는:

- **unstable 파라미터도 `equals()` 비교로 skip된다.** 과거처럼 "unstable이면 항상 recompose"가 아니다.
- 따라서 Compiler 리포트의 `inferredUnstableClasses` 수치와 실제 recompose 발생 여부의 상관이 크게 약해졌다.
- 결과적으로 "unstable 클래스 수를 줄이는 것" 자체는 최종 KPI가 아니다.

### 2.2 과거 시도에서 얻은 실측 결과 (`feat/compose-immutable`)

네 개 UiState/Repository (`TableTheme` 계열, `ThemeRepository`, `HomeDrawerUiState`,
`VacancyUiState.Loaded`)를 `ImmutableList` / `PersistentList`로 전환했을 때:

- `inferredUnstableClasses`: 소폭 감소
- **`skippableComposables`: 변화 없음 (876 / 1532 → 876 / 1532)**

즉 리포트 지표상의 개선이 **사용자 체감 recompose 감소로 이어지지 않았다.** 반면 다음 비용이 발생했다:

- `DataWithStateUtil`에 `@JvmName` 오버로드 누적
- Preview·테스트·ViewModel update 로직 전반에 타입 전파
- `.toImmutableList()` 변환 지점 증가 (도메인 → UiState 경계)

이를 근거로 **일괄 `ImmutableList` 전환 전략은 중단**했고, 해당 브랜치는 폐기됐다.

### 2.3 첫 번째 가설 검증 결과 — `List<LectureSession>`

리포트상 가장 의심스러운 체인이었던 `LectureSession`(`LocalTime` 보유 → unstable)이 `SearchedLecture`/`LocalLecture`의 `lectureSessions` 필드를 타고 `TimeTable(lectures: List<LocalLecture>)` 같은 hot path 파라미터까지 전파되는 케이스를 검증 (`app/src/test/java/com/wafflestudio/snutt2/compose/stability/LectureSessionsSkipTest.kt`).

- 같은 `List` 참조 재전달 시 → skip 통과 (identity)
- 새 인스턴스지만 내용 동일한 `List` 전달 시 → skip 통과 (StrongSkipping의 equals 경로)

**결론**: 현재 프로젝트 조건에서 `LectureSession`에 `@Immutable`을 부착하거나 관련 리스트를 `ImmutableList`로 전환할 필요 없음. StrongSkipping + data class auto-equals만으로 skip이 충분히 일어남. 테스트는 회귀 방지용으로 보존된다.

### 2.4 `@Immutable` 어노테이션 vs 타입 교체

두 접근 모두 장단이 있다.

- **`@Immutable`**: 개발자의 선언. 컴파일러가 검증하지 않으므로 코드 리뷰에서 mutable 필드 추가를 감시해야 한다. 적용 비용이 낮음.
- **`ImmutableList`**: 타입 수준의 보장. 컴파일러가 mutate 연산을 에러로 드러낸다. 적용 비용이 높음 (Preview·테스트·경계 전파).

UiState 수준의 data class에서 `val xs: List<T>`는 외부에서 mutate 불가하므로 후자의 안전성 이점이 실질적으로 크지 않다. **기본은 `@Immutable`**이 더 가볍다.

---

## 3. 측정

세 가지 도구를 역할에 맞게 사용한다. 단일 "1차 지표"는 없으며, **발견·후보분석·검증**을 서로 다른 도구가 담당한다.

| 도구                                    | 역할                                       | 자동화 가능 여부                |
|---------------------------------------|------------------------------------------|-------------------------|
| Layout Inspector                      | 실사용 플로우에서 recompose 과다 Composable **발견**  | 불가 (Android Studio GUI) |
| Compose Compiler Reports              | 어떤 파라미터가 unstable로 추론되는지 **원인 분석/후보 좁히기** | 가능                      |
| Recompose 카운터 계측 테스트                  | 특정 skip 가설 **검증** 및 회귀 방지                | 가능                      |

### 3.1 Layout Inspector — 발견

실사용 플로우에서 recompose 과다를 **발견**하는 유일한 방법. 다만 수동 작업이므로 개발자가 직접 수행한다.

1. Android Studio → Layout Inspector 실행
2. 툴바의 "Show Recomposition Counts" 토글 on
3. 의심 화면에서 인터랙션(스크롤·버튼 탭·상태 변경 등)을 수행하며 카운트 관찰
4. 비정상적으로 높은 카운트를 가진 Composable을 식별

### 3.2 Compose Compiler Reports — 후보 분석

Layout Inspector에서 발견된 Composable의 원인 파라미터를 찾거나, 리포트 자체에서 역으로 "위험 후보"를 좁힐 때 사용한다.

리포트 재생성:

```bash
./gradlew :app:compileStagingDebugKotlin -PcomposeCompilerReports=true --rerun-tasks --no-configuration-cache
```

리포트 파일:

- `app/build/compose_compiler/app-classes.txt` — 클래스별 stability 추론 결과
- `app/build/compose_compiler/app-composables.txt` — Composable 파라미터 stability
- `app/build/compose_compiler/stagingDebug/app-module.json` — 요약 수치

특정 Composable/클래스 상세 보기:

```bash
grep -A8 "unstable class com.wafflestudio.snutt2.feature.xxx.XxxUiState" app/build/compose_compiler/app-classes.txt
```

### 3.3 Recompose 카운터 테스트 — 가설 검증

"이 Composable은 `X` 필드 변경 시 skip되어야 한다" 같은 가설을 **자동으로 검증**한다. Layout Inspector 없이도 recompose 동작을 측정할 수 있는 경로.

**실행 환경**: 이 프로젝트에서는 JVM 단위 테스트에서 Robolectric + `compose-ui-test-junit4`로 돌린다 — 에뮬레이터·디바이스 불필요. 인프라 셋업은 `app/build.gradle.kts`의 `testImplementation(libs.robolectric)` · `debugImplementation(libs.compose.ui.test.manifest)` · `app/src/test/resources/robolectric.properties`를 참고.

**작동 원리**: 측정 대상 Composable 내부에 `SideEffect { counter.increment() }`를 끼워 넣으면 해당 Composable이 재조합될 때마다 증가한다. 무관한 상태 변경 후에도 카운터가 증가하면 skip에 실패한 것.

**실제 패턴** (`app/src/test/java/com/wafflestudio/snutt2/compose/stability/LectureSessionsSkipTest.kt` 참조):

```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SomeSkipTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun `consumer skips on unrelated parent state change`() {
        val counter = RecomposeCounter()
        val triggerState = mutableStateOf(0)
        val fixedInput = makeInput()

        composeRule.setContent {
            @Suppress("UNUSED_VARIABLE")
            val t = triggerState.value // 부모가 상태를 읽어 recompose 유도
            Consumer(input = fixedInput, counter = counter)
        }
        composeRule.waitForIdle()
        val baseline = counter.count

        triggerState.value = 1
        composeRule.waitForIdle()

        assertEquals(expected = baseline, actual = counter.count, message = "...")
    }
}

@Stable
private class RecomposeCounter {
    var count = 0
        private set

    fun increment() { count++ }
}

@Composable
private fun Consumer(input: SomeType, counter: RecomposeCounter) {
    SideEffect { counter.increment() }
    // ...
}
```

**주의 / 설계 포인트**

- **카운터는 람다가 아니라 `@Stable` 클래스 인스턴스로 넘긴다.** `var count = 0`을 캡처하는 람다는 stable로 추론되지 않아 부모 recompose마다 새 인스턴스가 생성되고, 테스트하려는 skip 자체를 깨뜨린다.
- `waitForIdle()`로 Composition이 안정될 때까지 기다린 뒤 baseline을 찍고 delta로 비교한다.
- 테스트 작성 전에 **가설**이 명확해야 한다 ("X 상태 변경 시 Y Composable은 skip돼야 한다"). 가설 없는 탐색은 Layout Inspector가 적합.

### 3.4 자동 워크플로

개발자 수동 개입 없이 진행 가능한 경로는 다음과 같다:

```
Compose Compiler Reports로 위험 후보 식별 (자동)
        ↓
가설 수립 ("X Composable은 Y 필드 변경 시 skip되어야 한다")
        ↓
Recompose 카운터 테스트 작성 (자동)
        ↓
    ┌─ 테스트 통과: 가설 확인. 조치 불필요 (이미 skip됨)
    │
    └─ 테스트 실패: 실제 skip 실패
            ↓
         4장의 대응 수단 적용
            ↓
         테스트 재실행 → 회귀 방지용 영구 보존
```

Layout Inspector는 이 루프에 없는, 사용자 체감 문제가 먼저 발생한 경우의 진입점이다.

### 3.5 방어 테스트 세트 — 반자동 불변식 감시

**목적**: Layout Inspector로 문제를 발견하기 전에, "**전반적 skip 전략이 무너지는 변경**"이 들어오면 CI가 즉시 실패하도록 한다. 개별 Composable을 빠짐없이 커버하는 것이 목적이 아니다. 소수의 **canary 테스트**만 유지한다.

**위치**: `app/src/test/java/com/wafflestudio/snutt2/compose/stability/`

**구성 (상한 3–5건)**:

1. **StrongSkipping 스모크 테스트 (1건, 필수)** — unstable 클래스 인스턴스를 값이 같은 새 인스턴스로 교체했을 때 consumer Composable이 skip되는지 검증. StrongSkipping 플래그가 꺼지거나 Compose 컴파일러가 예상과 달리 동작하면 이 테스트 하나가 먼저 깨진다.
2. **대표 타입 skip 테스트 (2–3건)** — 프로젝트의 skip 전략을 대표하는 **hot path 타입** 한 건씩:
    - UiState data class (예: `HomeDrawerUiState`) — auto-equals 정합성 보장
    - 핵심 unstable 도메인 모델의 리스트 전달 (예: `LectureSessionsSkipTest` — `List<LectureSession>`)
    - Sealed/`@Immutable` 타입 대표 1건 (예: `DialogState` 계열)

**선정 기준**

- **커버리지 지향 ❌**: "unstable 클래스 N개 있으니 테스트도 N개" 식 확장 금지. 과거 ImmutableList 일괄 전환과 같은 실수가 된다.
- **레버리지 지향 ⭕**: 이 한 테스트가 깨지면 "해당 타입뿐 아니라 같은 패턴의 수십 개 Composable이 같이 회귀했다"고 판단할 수 있는 지점.
- 진입점 A·B(§5)의 가설 검증에서 **이미 만들어진** recompose 카운터 테스트는 그 자리에 영구 보존된다. 방어 테스트 세트는 그와 별개로 **선제적으로** 유지하는 최소 세트를 지칭한다.

**유지 비용 상한**

- 전체 3–5건을 넘기지 않는다. 넘으면 기존 것을 덜어내거나 병합한다.
- 새 테스트 추가는 "**기존 canary로는 못 잡는 회귀 시나리오**"가 명확할 때만.

---

## 4. 대응 수단

실측(Layout Inspector 또는 recompose 카운터 테스트 실패)으로 병목이 확인된 경우에만, 병목 원인에 맞춰 다음 중 **우선순위대로** 선택한다.

### 4.1 `@Immutable` 어노테이션 부착 (우선)

**적용 조건**: data class의 필드가 실질적으로 불변이지만 컴파일러의 stability 추론이 실패하는 경우.

**전형 사례**:

- `java.time.LocalTime` / `LocalDate` 등 stdlib 타입을 필드로 가짐 (컴파일러가 외부 타입 stability를 모름)
- `List<T>` 필드지만 외부에서 mutate하지 않음을 확신할 수 있음

```kotlin
@Immutable
data class LectureSession(
    val startTime: LocalTime,
    val endTime: LocalTime,
    ...
)
```

**주의**: 선언이므로 컴파일러가 검증하지 않는다. 해당 data class에 `var` 필드나 `MutableList` 필드를 추가하지 않도록 코드 리뷰에서 감시한다.

### 4.2 UiState 경계에서 `ImmutableList` / `PersistentList` (차선)

`@Immutable`로 해결되지 않고, **리스트 필드가 실제 recompose 원인임이 확인된** 경우.

원칙:

- **UiState 경계에서만 적용**한다. 도메인 모델·Repository 반환 타입·DTO까지 전파하지 않는다.
- ViewModel에서 단순 노출만 하는 경우: `ImmutableList<T>`
- ViewModel에서 `add` / `removeAt` / `set` 등 변이가 필요한 경우: `PersistentList<T>` (structural sharing)

변환은 UiState 조립 시점에 `.toImmutableList()`로 한 번만 수행한다.

### 4.3 기타 일반 기법

- 파라미터를 `remember`로 메모화
- Composable을 더 작은 단위로 분리해 recompose 범위 축소
- `key()` 블록 사용
- lambda 캡처 최소화 (`remember { { ... } }`)

---

## 5. 의사결정 플로우

### 5.1 진입점 A — 사용자 체감 문제 선행

```
특정 화면에서 렉·끊김이 의심됨
        ↓
Layout Inspector로 recomposition count 확인
        ↓
    ┌───── 카운트 정상 ────→ 조치 불필요. List 유지
    │
    └───── 카운트 비정상
            ↓
         리포트로 원인 파라미터 특정
            ↓
         (가능하면) 회귀 방지용 recompose 카운터 테스트 작성
            ↓
        ┌─ 필드 불변인데 추론 실패 ────→ @Immutable
        │
        ├─ 리스트가 원인 (경계) ───────→ UiState에서 ImmutableList
        │
        └─ 그 외 (lambda, 세밀한 키 등) → remember / 분리 / key
```

### 5.2 진입점 B — 리포트 주도 선제 검증 (자동)

```
리포트에서 위험 후보 식별 (unstable 파라미터 + 자주 변할 법한 state)
        ↓
"이 Composable은 무관한 state 변경 시 skip돼야 한다" 가설 수립
        ↓
Recompose 카운터 계측 테스트 작성
        ↓
    ┌─ 통과 ────→ 가설 확인. 테스트는 회귀 방지용 영구 보존
    │
    └─ 실패 ────→ 진입점 A의 "카운트 비정상" 이후와 동일
```

진입점 B는 Layout Inspector 없이도 수행 가능하지만, **모든 Composable에 대해 하지 않는다**. 리포트에서 위험도가 높다고 판단된 후보에 한해서만. "unstable 파라미터 하나 = 테스트 하나"는 원래 거부했던 일괄 작업과 다르지 않다.

### 5.3 진입점 C — 방어 테스트 세트 (상시)

```
CI에서 §3.5의 canary 테스트가 실패
        ↓
    ┌─ StrongSkipping 스모크 실패
    │   → 컴파일러 플래그·버전·빌드 구성 변경 의심. 먼저 확인.
    │
    └─ 대표 타입 테스트 실패
        → 해당 타입의 equals 깨짐 / @Immutable 제거 / 필드 추가 등 회귀
        → 진입점 A의 "카운트 비정상" 이후와 동일하게 처리
```

방어 테스트는 **문제 발견 도구가 아니라 회귀 감지 센서**다. 개별 skip 보장이 필요하면 진입점 A/B로 가서 해당 Composable용 전용 테스트를 추가한다.

---

## 6. 작업 시 원칙

1. **커밋 서명·Co-Authored-By 금지** (프로젝트 공통 규칙)
2. **"일단 ImmutableList로 바꾸자" 금지**. 실측 근거 없이 선제적 전환하지 않는다.
3. 논리적 관심사 하나 = 1 커밋.
4. 대응 수단을 적용할 때는 **가능하면 recompose 카운터 계측 테스트를 먼저 작성**해 실패를 확인한 뒤 적용한다 (TDD 스타일). 적용 후 테스트가 통과하면 회귀 방지용으로 보존한다.
5. §3.5 방어 테스트 세트는 **3–5건 상한**을 지킨다. 타입마다 추가하지 않는다. 추가 시에는 기존 canary로 못 잡는 시나리오인지 먼저 확인한다.

---

## 7. 참고 문서

- [`color-theme-policy.md`](./color-theme-policy.md) — 테마 도메인 모델 설계 배경
- [`viewmodel-test-setup-policy.md`](./viewmodel-test-setup-policy.md) — 테스트 setup 구조
- `CLAUDE.md` (프로젝트 루트) — 전체 아키텍처 원칙
