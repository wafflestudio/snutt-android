## **기본 구조**

안드로이드 권장 아키텍처를 따른다.

`View - ViewModel - Repository - Data source`

의존 방향: 각 레이어는 바로 아래 레이어에만 의존한다. ViewModel → Repository → Data source. 레이어를 건너뛰는 의존(예: ViewModel → Data source)은 허용하지 않는다.

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

- 각 Route가 가질 수 있는 상태를 적절히 분류해서 sealed interface 로 표현
    - 예: Success / Loading / Empty / Error
    - 하나밖에 없다면 data class 로 바로 표시
- 각 개별 하위 상태별 UI를 그리기 위한 모든 값을 필드로 표현
- 다이얼로그·바텀시트 등 "어떤 UI를 띄울지"에 대한 상태도 UiState의 일부로 포함한다.
    - 보통 nested sealed interface (예: `DialogState`, `SheetType`) 로 표현

사용

- 인스턴스 생성은 오직 ViewModel이 담당
- ViewModel은 `private val _uiState: MutableStateFlow<UiState>` 하나만 갖는다.
    - `val uiState: StateFlow<UiState> = _uiState.asStateFlow()` 로 노출
    - 별도 StateFlow 변수(내부 상태 전용 data class를 담은 MutableStateFlow 등)를 추가로 두지 않는다.
    - 모든 상태 변경은 `_uiState.update { current -> ... }` 로 처리한다.
        - 사용자 이벤트: 이벤트 핸들러 함수에서 `_uiState.update` 직접 호출
        - 외부 데이터 변화: init의 combine collectLatest에서 `_uiState.update` 호출

UiState를 구성하는 외부 데이터는 종류에 따라 아래와 같이 처리한다.

| 종류 | 설명 | 처리 방식 |
|---|---|---|
| **A. 동기 구독** | Repository/UseCase의 StateFlow | init에서 `combine`으로 묶어 `_uiState.update` |
| **B-1. 초기 블로킹 API** | 첫 진입 시 데이터 로드 전까지 Loading 유지해야 하는 1회 API | `flow { emit(null); emit(apiCall()) }` 형태로 A와 함께 `combine`에 포함. null이면 Loading 유지. |
| **B-2. 비동기 1회 API** | 비동기로 늦게 반영돼도 되는 1회 API | `private suspend fun` → init에서 async 호출 → A 타입 StateFlow 갱신 |
| **C-1. UI 이벤트 트리거 refetch** | UI 이벤트 발생 시 refetch가 필요한 데이터 | `private suspend fun` → 이벤트 핸들러에서 호출 |
| **C-2. 내부 로직 트리거 refetch** | 내부 상태 변경(학기 변경 등)으로 refetch가 필요한 데이터 | `flatMapLatest` 형태로 `combine`에 포함 |

- A, B-1, C-2는 init에서 하나의 `combine`으로 묶어 처리한다.
- B-1과 C-2가 겹치는 경우(예: 초기 로드 + 내부 key 변경 시 재조회)는 `flatMapLatest`로 동시 처리한다.
- B-2, C-1은 `private suspend fun`으로 분리하고, init에서 비동기 호출하거나 이벤트 핸들러에서 호출한다.
- B-1의 API 호출 실패 시 combine 전체가 멈추지 않도록 flow 내부에서 예외를 처리해야 한다.

**UiEvent**

의미

- 한 번 소비되고 사라지는 일회성 이벤트 (토스트, 네비게이션, 바텀시트 열기/닫기 등)

원칙

- UiState는 영속적인 상태, UiEvent는 소비 후 사라지는 이벤트로 역할을 명확히 구분한다.
- 컴포즈 UI 라이브러리의 상태(예: `ModalBottomSheetState`)는 Route가 소유한다. ViewModel은 직접 제어하지 않고 UiEvent를 통해 제어를 요청한다.

---

### **Data 레이어**

**Repository**

역할

- data source 로의 요청 및 data source 로부터의 데이터 수신을 추상화
- (SNUTTStorage 한정) SNUTTStorage의 StateFlow 를 단순 전달

원칙

- 서버 스펙에서 기원하는 관심사(API에 전달할 ID 결정, DTO 필드 매핑 등)는 Repository(data layer)에서 처리한다. ViewModel/도메인 로직이 이를 알아서는 안 된다.
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