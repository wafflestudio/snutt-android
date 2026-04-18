# Compose Stability 정책

이 문서는 SNUTT Android의 Compose recompose 최적화 정책을 기술한다.

---

## 1. 기본 방침

- **기본형은 stdlib `List<T>`**. 선제적으로 `ImmutableList`/`PersistentList`로 전환하지 않는다.
- **실측으로 확인된 병목**에 한해, 원인을 분석한 뒤 최소 침습적으로 대응한다.
- 실측은 두 경로를 병행한다: (a) Layout Inspector — 유저 주도 발견 (b) recompose 카운터 계측 테스트 — 가설 검증 및 회귀 방지.

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

### 2.3 `@Immutable` 어노테이션 vs 타입 교체

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

### 3.3 Recompose 카운터 계측 테스트 — 가설 검증

"이 Composable은 `X` 필드 변경 시 skip되어야 한다" 같은 가설을 **자동으로 검증**한다. Layout Inspector 없이도 recompose 동작을 측정할 수 있는 경로.

**작동 원리**: Composable 내부에 `SideEffect { counter++ }`를 끼워 넣으면 해당 Composable이 재조합될 때마다 증가한다. 무관한 상태 변경 후에도 카운터가 증가하면 skip에 실패한 것.

**예시 스켈레톤** (`app/src/androidTest/...`):

```kotlin
@get:Rule val composeRule = createComposeRule()

@Test
fun tableScene_skipsOnUnrelatedFieldChange() {
    var recomposeCount = 0
    val state = mutableStateOf(initialUiState)

    composeRule.setContent {
        TableScene(
            uiState = state.value,
            recomposeProbe = { recomposeCount++ }, // 측정을 위해 파라미터로 주입
        )
    }
    composeRule.waitForIdle()
    val baseline = recomposeCount

    state.value = initialUiState.copy(unrelatedField = "changed")
    composeRule.waitForIdle()

    assertEquals("Scene이 unrelatedField 변경에 recompose되면 안 됨", baseline, recomposeCount)
}
```

**프로브 주입 방식** (두 가지):

- **파라미터 주입 (권장)**: 측정 대상 Composable이 `recomposeProbe: () -> Unit = {}` 같은 기본값 람다를 받음. 프로덕션 콜사이트는 기본값 사용, 테스트 콜사이트만 주입. 타입이 명시적.
- **내부 카운터 노출**: 대상을 건드리지 않고 래퍼로 감싸기. 덜 침습적이지만 "프로브된 Composable 자체의 recompose"만 측정 가능, 하위 자식은 못 봄.

**주의**

- **`waitForIdle()`**로 Composition이 안정될 때까지 기다려야 함. 첫 Composition 때 카운터가 1 이상이 되므로 baseline을 찍고 delta를 본다.
- 계측 테스트라 에뮬레이터/디바이스 필요. CI 연동 시 Gradle Managed Device 권장.
- 테스트를 작성하기 전에 **가설**이 명확해야 한다 ("X 필드 변경 시 Y Composable은 skip되어야 한다"). 가설 없는 탐색은 Layout Inspector가 적합.

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

---

## 6. 작업 시 원칙

1. **커밋 서명·Co-Authored-By 금지** (프로젝트 공통 규칙)
2. **"일단 ImmutableList로 바꾸자" 금지**. 실측 근거 없이 선제적 전환하지 않는다.
3. 논리적 관심사 하나 = 1 커밋.
4. UiState 경계에서 `ImmutableList`를 쓰기로 했다면, 도메인 레이어까지 끌고 내려가지 않는다. `.toImmutableList()`는 UiState 조립 지점에서 한 번만.
5. 대응 수단을 적용할 때는 **가능하면 recompose 카운터 계측 테스트를 먼저 작성**해 실패를 확인한 뒤 적용한다 (TDD 스타일). 적용 후 테스트가 통과하면 회귀 방지용으로 보존한다.

---

## 7. 참고 문서

- [`color-theme-policy.md`](./color-theme-policy.md) — 테마 도메인 모델 설계 배경
- [`viewmodel-test-setup-policy.md`](./viewmodel-test-setup-policy.md) — 테스트 setup 구조
- `CLAUDE.md` (프로젝트 루트) — 전체 아키텍처 원칙
