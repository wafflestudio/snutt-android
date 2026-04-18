## **기본 구조**

안드로이드 권장 아키텍처를 따른다.

`View - ViewModel - Repository - Data source`

의존 방향: 각 레이어는 바로 아래 레이어에만 의존한다. ViewModel → Repository → Data source. 레이어를 건너뛰는 의존(예: ViewModel →
Data source)은 허용하지 않는다.

---

### **UI 레이어**

Jetpack Navigation 을 뼈대로 한다.

**Route**

의미

- Jetpack Navigation에서의 route 와 1:1 매핑되는 컴포저블 함수

역할

- HiltViewModel 을 생성하고, viewModel과 의사소통
- viewModel 로부터 UiState 를 구독하고 하위 UI 컴포넌트에게 전달
- viewModel 로부터 UiEvent 를 구독해서 적절히 처리
- 하위 요소에서 발생한 이벤트를 적절히 ViewModel 에게 전달
- 하위 요소에서 발생한 이벤트 중 navigation 관련된 이벤트는 상위로 올림

**Screen**

의미

- 로직과 상태를 (가급적) 갖지 않는 순수한 UI 요소로, 가장 큰 단위의 “화면” 에 해당

역할

- uiState 를 전달받아서 하위 컴포넌트들에게 전달
- 하위 컴포넌트에서 발생한 이벤트를 부모(Route)에게 전달함

원칙

- 다음 두 가지 조건을 만족하는 값만 Screen 및 하위 컴포저블이 상태로 가질 수 있다.
    - 비즈니스 로직과 전혀 무관
    - 외부 라이프사이클에 따라 유지 혹은 복구될 필요 없음

**ViewModel**

의미

- 상태를 가지고, 비즈니스 로직을 담당하는 HiltViewModel
- Jetpack Navigation 에서 route 별로 유일하게 존재

역할

- 각 Route 별 화면(기능 단위)에 필요한 모든 UI 상태를 단일 데이터 클래스의 StateFlow로 제공
- Route로부터 이벤트 발생을 전달받고 로직에 따라 처리
    - 상태를 다시 변경하거나,
    - data source 로 요청을 전송하거나,
    - UiEvent 를 발생시키거나.

원칙

- UiState와 무관하게 비즈니스 로직에만 사용되는 값(주로 SavedStateHandle로 주입된 값)은 UiState 밖에 ViewModel 멤버 변수로 둔다.

**UiState**

의미

- 각 Route 별 UI의 모든 상태를 나타내는 단일 인터페이스

역할

- 각 Route가 가질 수 있는 상태를 적절히 분류해서 표현
- 각 개별 하위 상태별 UI를 그리기 위한 모든 값을 필드로 표현
- 다이얼로그·바텀시트 등 "어떤 UI를 띄울지"에 대한 상태도 UiState의 일부로 포함한다.
    - 보통 nested sealed interface (예: `DialogState`, `SheetType`) 로 표현

구조

UiState의 형태는 화면의 특성에 따라 결정한다.

(a) **콘텐츠 로딩 분기가 없는 경우**: data class 하나로 표현한다.

```kotlin
data class SettingsUiState(
    val themeMode: ThemeMode = ThemeMode.AUTO,
    val dialogState: DialogState = DialogState.None,
)
```

(b) **콘텐츠 로딩 분기가 있고, UI 인터랙션 상태(다이얼로그, 편집 모드 등)가 콘텐츠 전이와 무관하게 유지되어야 하는 경우**: 최상위를 data class로 두고,
콘텐츠 로딩 상태만 nested sealed interface(`ContentState`)로 분리한다.

```kotlin
data class VacancyUiState(
    val contentState: ContentState = ContentState.Loading,
    val dialogState: DialogState = DialogState.None,
    val isEditMode: Boolean = false,
) {
    sealed interface ContentState {
        data object Loading : ContentState
        data object Error : ContentState
        data object Empty : ContentState
        data class Loaded(...) : ContentState
    }
}
```

이 구조의 장점:

- 외부 데이터 갱신 시 `it.copy(contentState = ...)`로 콘텐츠만 교체하면 dialogState 등 UI 인터랙션 상태가 자동 보존된다.
- Loading 상태에서도 다이얼로그를 열 수 있다.
- `showDialog()`, `dismissDialog()` 등이 콘텐츠 상태 분기 없이 단순 `it.copy(dialogState = ...)`로 구현된다.

(c) **콘텐츠 로딩 분기가 있지만, UI 인터랙션 상태가 없거나 극히 단순한 경우**: 최상위를 sealed interface로 표현해도 무방하다.

```kotlin
sealed interface PushPreferencesUiState {
    data object Loading : PushPreferencesUiState
    data class Success(...) : PushPreferencesUiState
    data object Error : PushPreferencesUiState
}
```

단, UI 인터랙션 상태가 추가되면 (b)로 전환한다.

사용

- 인스턴스 생성은 오직 ViewModel이 담당
- ViewModel은 `private val _uiState: MutableStateFlow<UiState>` 하나만 갖는다.
    - `val uiState: StateFlow<UiState> = _uiState.asStateFlow()` 로 노출
    - 별도 StateFlow 변수(내부 상태 전용 data class를 담은 MutableStateFlow 등)를 추가로 두지 않는다.
    - 모든 상태 변경은 `_uiState.update { current -> ... }` 로 처리한다.
        - 사용자 이벤트: 이벤트 핸들러 함수에서 `_uiState.update` 직접 호출
        - 외부 데이터 변화: init에서 combine 또는 collect를 통해 `_uiState.update` 호출

UiState를 구성하는 외부 데이터는 종류에 따라 아래와 같이 처리한다.

| 종류                          | 설명                                       | 처리 방식                                                                                   |
|-----------------------------|------------------------------------------|-----------------------------------------------------------------------------------------|
| **A. 동기 구독**                | Repository/UseCase의 StateFlow            | init에서 `combine`으로 묶어 `_uiState.update`                                                 |
| **B-1. 초기 블로킹 API**         | 첫 진입 시 데이터 로드 전까지 Loading 유지해야 하는 1회 API | `flow { ... }`로 fetch 완료까지 emission을 지연. A와 함께 `combine`에 포함하거나 단독 `collect`. 아래 상세 참조. |
| **B-2. 비동기 1회 API**         | 비동기로 늦게 반영돼도 되는 1회 API                   | `private suspend fun` → init에서 async 호출 → A 타입 StateFlow 갱신                             |
| **C-1. UI 이벤트 트리거 refetch** | UI 이벤트 발생 시 refetch가 필요한 데이터             | 이벤트 핸들러에서 suspend 호출                                                                    |
| **C-2. 내부 로직 트리거 refetch**  | 내부 상태 변경(학기 변경 등)으로 refetch가 필요한 데이터     | `flatMapLatest` 형태로 `combine`에 포함                                                       |

B-1 패턴은 Repository API 형태에 따라 두 가지:

(a) API가 데이터를 직접 반환 (`getX(): Result<T>`):

```kotlin
flow { emit(null); emit(repository.getX()) }
```

combine에서 `null`이면 Loading 유지, non-null이면 데이터 처리. API 호출 실패 시 combine 전체가 멈추지 않도록 flow 내부에서 예외를 처리해야
한다.

(b) Repository가 fetch + StateFlow 제공 (`fetchX(): Result<Unit>` + `val x: StateFlow<T>`):

```kotlin
flow {
    when (val result = repository.fetchX()) {
        is Result.Success -> emitAll(repository.x)
        is Result.Fail -> { /* Error 처리 후 flow 종료 */
        }
    }
}
```

fetch 완료 전까지 emission이 발생하지 않아 Loading 유지. 성공 시 StateFlow 구독 시작, 실패 시 Error 설정 후 flow 종료.

- A, B-1, C-2는 init에서 하나의 `combine`으로 묶어 처리한다. A가 하나이고 C-2가 없으면 단독 `collect`도 가능.
- B-1과 C-2가 겹치는 경우(예: 초기 로드 + 내부 key 변경 시 재조회)는 `flatMapLatest`로 동시 처리한다.
- B-2는 `private suspend fun`으로 분리하고 init에서 비동기 호출한다. C-1은 이벤트 핸들러에서 `viewModelScope.launch`로 호출한다.

**UiEvent**

의미

- 한 번 소비되고 사라지는 일회성 이벤트 (토스트, 네비게이션, 바텀시트 열기/닫기 등)

원칙

- UiState는 영속적인 상태, UiEvent는 소비 후 사라지는 이벤트로 역할을 명확히 구분한다.
- 컴포즈 UI 라이브러리의 상태(예: `ModalBottomSheetState`)는 Route가 소유한다. ViewModel은 직접 제어하지 않고 UiEvent를 통해 제어를
  요청한다.

**Preview / Screenshot Test**

`main` 의 `@Preview` 와 `screenshotTest` 의 `@Preview` 는 역할이 명백히 다르다. 세부 규약(도구, 커버리지 기준, 네이밍, 인라인 휴리스틱)은
`docs/screenshot-test-policy.md` 참조.

원칙

- `main` 의 `@Preview`: 개발자의 시각적 단서 목적. 해당 컴포넌트가 어떻게 생겼는지 인지할 수 있을 정도의 최소 분기만 포함.
- `screenshotTest` 의 `@Preview`: 코드 변경에 따른 의도치 않은 시각 회귀를 방어하는 논리적 방어막. 유의미한 모든 분기를 1:1로 커버한다.
- 커버리지 단위는 **컴포넌트**. 플로우/Screen 단위 테스트는 현 범위에서 제외한다.
- "유의미한 분기" 는 파라미터의 곱집합이 아니라 **실제 `when`/`if` 분기 로직이 바뀌는 경로**를 말한다.
- `X.kt` ↔ `XScreenshotTest.kt` 1:1 대응. 테스트 preview 이름은 `{ComponentName}_{분기식별자}`.
- 별도 컴포저블인데 (a) 스킵 경계·접근성 등 정당 사유가 없고 (b) 유의미한 시각 분기가 0~1개면 인라인 코드 스멜로 본다.

**ModalBottomSheet**

`ModalBottomSheetLayout` 사용 시 ViewModel의 UiState와 Compose의 `ModalBottomSheetState`라는 이중 상태가 발생한다. 상태
괴리를 최소화하기 위해 아래 원칙을 따른다. 의사결정 배경은 `docs/bottom-sheet-policy.md` 참조.

원칙

- `rememberModalBottomSheetState`로 생성하는 `sheetState`는 Route가 소유하되, 속성 직접 접근(`isVisible` 등)은 금지한다.
  `ModalBottomSheetLayout`에 전달하고 UiEvent 핸들러에서 `show()`/`hide()`를 호출하기 위한 배관(plumbing)으로만 취급한다.
- `sheetState`의 상태 변경(`show()`/`hide()`)은 UiEvent 구독 핸들러 내에서만 수행한다.
- 바텀시트를 사용하는 Route는 `BottomSheetDismissEffect`를 반드시 사용하여, 바텀시트가 닫힌 뒤(애니메이션 완료 후) ViewModel의 정리 함수(
  `onSheetDismissed`)를 호출한다.
- UiState에서 바텀시트 상태(SheetType 등)를 None/Empty로 바꾸는 것은 `BottomSheetDismissEffect`의 side-effect로만 수행한다.
  바텀시트를 닫는 시점에 즉시 변경하지 않는다.
- BackHandler에서 바텀시트 열림 여부를 판단할 때는 UiState의 SheetType 필드를 사용한다.
- BackHandler는 Route에서만 사용한다.
    - 예외: 하위 컴포저블이 자체 로컬 상태 기반의 서브 네비게이션을 가지는 경우 (예: `TimeSelectSheet`의 시간 선택 모드)

예시

```kotlin
// Route
@Composable
fun ExampleRoute(vm: ExampleViewModel = hiltViewModel()) {
    val uiState by vm.uiState.collectAsStateWithLifecycle()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true,
    )

    BackHandler(enabled = uiState.sheetType != SheetType.None) {
        vm.closeSheet()
    }

    BottomSheetDismissEffect(sheetState, vm::onSheetDismissed)

    LaunchedEffect(Unit) {
        vm.uiEvent.collect { event ->
            when (event) {
                is UiEvent.OpenSheet -> sheetState.show()
                is UiEvent.CloseSheet -> sheetState.hide()
            }
        }
    }

    ExampleBottomSheetLayout(sheetState = sheetState, ...) { ... }
}

// ViewModel
fun openSheet(data: Data) {
    _uiState.update { it.copy(sheetType = SheetType.Detail(data)) }
    viewModelScope.launch { _uiEvent.emit(UiEvent.OpenSheet) }
}

fun closeSheet() {
    viewModelScope.launch { _uiEvent.emit(UiEvent.CloseSheet) }
}

fun onSheetDismissed() {
    _uiState.update { it.copy(sheetType = SheetType.None) }
}
```

---

### **Data 레이어**

**Repository**

역할

- data source 로의 요청 및 data source 로부터의 데이터 수신을 추상화
- (SNUTTStorage 한정) SNUTTStorage의 StateFlow 를 단순 전달

원칙

- 서버 스펙에서 기원하는 관심사(API에 전달할 ID 결정, DTO 필드 매핑 등)는 Repository(data layer)에서 처리한다. ViewModel/도메인 로직이 이를
  알아서는 안 된다.
- 캐시 무효화·재조회 등 데이터 갱신 시점 판단은 Repository가 투명하게 처리한다. ViewModel은 mutate 후 "refetch해야겠다"는 것을 신경 쓰지 않는다.
- 도메인 모델을 반환한다. DTO를 상위 레이어에 노출하지 않는다.
    - 단, 구 Repository 코드 중 일부는 아직 DTO를 반환하며, 추후 리팩토링 대상이다.
- 성공/실패는 예외를 throw하지 않고 `Result.Success` / `Result.Fail` 타입으로 반환한다.

---

### Date Source

실제 데이터의 원천 (서버 / 로컬 저장소)

**SNUTTStorage**

의미

- SharedPreference 기반으로 추상화되어 있는 로컬 저장소

역할

- 유저 로그인 토큰, 마지막으로 본 시간표 등 특정 데이터 저장 및 제공

**SNUTTRestApi**

의미

- Retrofit 용으로 작성된 서버 API 명세 (혹은 그 구현체)

역할

- SNUTT 서버와의 통신