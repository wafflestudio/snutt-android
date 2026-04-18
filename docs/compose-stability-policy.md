# Compose Stability 정책

이 문서는 SNUTT Android의 Compose recompose 최적화 정책을 기술한다.

---

## 1. 기본 방침

- **기본형은 stdlib `List<T>`**. 선제적으로 `ImmutableList`/`PersistentList`로 전환하지 않는다.
- **Layout Inspector로 실측된 병목**에 한해, 원인을 분석한 뒤 최소 침습적으로 대응한다.

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

### 3.1 1차 지표 — Layout Inspector Recomposition Count

**이것이 진짜 KPI다.**

1. Android Studio → Layout Inspector 실행
2. 툴바의 "Show Recomposition Counts" 토글 on
3. 의심 화면에서 인터랙션(스크롤·버튼 탭·상태 변경 등)을 수행하며 카운트 관찰
4. 비정상적으로 높은 카운트를 가진 Composable을 식별

### 3.2 2차 지표 — Compose Compiler Reports (원인 분석용)

1차 지표에서 특정 Composable이 문제로 확인된 뒤, **어떤 파라미터가 unstable로 추론되는지** 확인할 때만 사용한다.

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

---

## 4. 대응 수단

Layout Inspector로 병목이 확인된 경우에만, 병목 원인에 맞춰 다음 중 **우선순위대로** 선택한다.

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
        ┌─ 필드 불변인데 추론 실패 ────→ @Immutable
        │
        ├─ 리스트가 원인 (경계) ───────→ UiState에서 ImmutableList
        │
        └─ 그 외 (lambda, 세밀한 키 등) → remember / 분리 / key
```

---

## 6. 작업 시 원칙

1. **커밋 서명·Co-Authored-By 금지** (프로젝트 공통 규칙)
2. **"일단 ImmutableList로 바꾸자" 금지**. 실측 근거 없이 선제적 전환하지 않는다.
3. 논리적 관심사 하나 = 1 커밋.
4. UiState 경계에서 `ImmutableList`를 쓰기로 했다면, 도메인 레이어까지 끌고 내려가지 않는다. `.toImmutableList()`는 UiState 조립 지점에서 한 번만.

---

## 7. 참고 문서

- [`color-theme-policy.md`](./color-theme-policy.md) — 테마 도메인 모델 설계 배경
- [`viewmodel-test-setup-policy.md`](./viewmodel-test-setup-policy.md) — 테스트 setup 구조
- `CLAUDE.md` (프로젝트 루트) — 전체 아키텍처 원칙
