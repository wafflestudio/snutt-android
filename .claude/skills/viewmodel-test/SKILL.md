---
name: viewmodel-test
description: >
  ViewModel 테스트를 작성하거나 기존 테스트를 점검하는 스킬. 두 가지 모드가 있다:
  (1) 작성 모드 — 대상 ViewModel을 분석하고 테스트를 새로 작성한다.
  (2) 검증 모드 — 기존 테스트가 규칙에 맞는지 ViewModel 소스와 교차 검증하고 위반을 수정한다.
  "뷰모델 테스트 작성", "ViewModel 테스트", "테스트 추가", "Fake 작성",
  "테스트 점검", "테스트 검증", "규칙 확인", "테스트 리뷰" 등의 요청 시 사용한다.
---

# ViewModel 테스트

| 사용자 요청 | 모드 |
|---|---|
| "테스트 작성해줘", "테스트 추가", "Fake 만들어줘" | **작성 모드** |
| "테스트 점검해줘", "규칙에 맞는지 확인", "테스트 리뷰" | **검증 모드** |

테스트의 목적은 ViewModel의 비즈니스 로직을 검증하는 것이다:
- public 함수 호출 시 UiState 전이, UiEvent 발행, Repository 호출이 올바른지
- source(Repository StateFlow 등) 변화 시 UiState가 올바르게 반영되는지

테스트 대상이 아닌 것: Repository 내부 로직, Compose UI, Navigation, Hilt DI

**두 모드 모두 "공통: 분석" 섹션부터 시작한다.**

---

# 공통: ViewModel 분석 + 기대 테스트 목록

## 1. ViewModel 소스 읽기

대상 ViewModel 소스를 읽고 다음을 파악한다:

| 항목 | 파악할 내용 |
|------|-----------|
| **UiState 구조** | (a) 단순 data class / (b) data class + ContentState / (c) sealed interface |
| **UiEvent** | sealed interface 멤버 전체 |
| **init 블록** | combine/collect 대상 source 목록, 비동기 작업(fetch 등) 유무 |
| **public 함수** | 각 함수의 역할 — Repository 호출, 상태 변경, 이벤트 발행 |
| **생성자 의존성** | Repository, UseCase, SavedStateHandle, 기타 interface |

## 2. Source map 구축

ViewModel의 init 블록과 모든 public 함수가 참조하는 Fake 상태를 나열한다.

예:
```
init combine:
  - tableDisplayRepository.tableTrimParam       (StateFlow)
  - tableDisplayRepository.compactMode          (StateFlow)
  - tableDisplayRepository.tableLectureCustomOption (StateFlow)
  - tableRepository.currentTable                (StateFlow, filterNotNull)
  - getCurrentTableThemeUseCase()               (Flow)
  로직: fittedTrimParam = if (forceFitLectures) getFittingTrimParam(Default) else tableTrimParam

toggleAutoTrim():
  - tableDisplayRepository.toggleForceFit() → toggleForceFitResult
  - 실패 시 handleError → ShowToast (일반 에러) / ShowToast + logout + NavigateToOnboard (AuthError)
```

## 3. 기대 테스트 목록 도출

source map으로부터 존재해야 할 테스트를 **빠짐없이** 열거한다.

### 나가는 방향: public 함수 → side-effect × 분기

각 public 함수에 대해:
1. 함수가 발생시키는 모든 side-effect를 나열한다.

| side-effect 종류 | 코드상의 표현 |
|---|---|
| **UiState 변경** | `_uiState.update { ... }` |
| **UiEvent 발행** | `_uiEvent.emit(...)` |
| **외부 의존성 호출** | Repository/UseCase의 suspend 함수 호출 |

2. 각 side-effect에 로직 분기(성공/실패, 조건 분기, 상태 분기 등)가 있으면 모든 분기를 전개한다.
3. **(side-effect × 분기) 조합 하나당 테스트 1개**가 있어야 한다.

### 들어오는 방향: source × 분기

들어오는 방향의 테스트는 두 종류로 나뉜다:

**(a) init 결과 테스트**: 모든 source의 초기값에 의한 최초 UiState 세팅을 검증한다. combine은 모든 source가 emit해야 첫 값을 내보내므로, 모든 source를 세팅하는 복합 테스트가 불가피하다. combine 로직에 분기가 있으면 분기별로 1개씩 작성한다. 기대 객체를 직접 구성한다 (패턴 2).

**(b) source 반응 테스트**: init 이후 개별 source의 값 변경에 의한 UiState 변경분만 검증한다. source별로 독립 테스트를 작성하고, before.copy로 변경된 필드만 검증한다 (패턴 1).

절차:
1. 생성자 의존성에서 ViewModel이 참조하는 source를 모두 나열한다 (Repository StateFlow, SavedStateHandle key, RemoteConfig Flow, UseCase Flow).
2. init combine/collect 내에서 각 source가 관여하는 로직을 파악한다.
3. 로직에 분기가 있으면 모든 분기를 전개한다.
4. init 결과 테스트: **(로직 분기) 하나당 테스트 1개**.
5. source 반응 테스트: **(source × 분기) 조합 하나당 테스트 1개**.

"source가 변하면 UiState가 갱신된다" 같은 피상적 검증이 아니라, **combine 로직의 구체적인 동작**을 검증해야 한다.

### 날카로운 테스트 값

각 테스트의 초기 Fake 상태와 함수 호출 인자는 **해당 테스트가 검증하고자 하는 동작을 날카롭게 드러내는 값**이어야 한다. 기본값이나 빈 값을 무심하게 넣지 않는다. 검증 대상 로직의 분기를 정확히 통과시키는, 의미 있는 값을 선택한다.

---

# 검증 모드 (공통 분석 이후)

## 1. 기존 테스트와 대조

기대 테스트 목록과 실제 테스트를 대조하여 **누락된 테스트**를 식별한다.

## 2. 기존 테스트 품질 체크

존재하는 각 테스트를 아래 규칙으로 체크한다. **테스트 코드만 읽으면 의미적 위반을 놓친다** — 반드시 source map과 대조한다.

| # | 규칙 | 검증 방법 |
|---|------|----------|
| 1 | **Fake 기본값 미의존** | 이 테스트가 건드리는 ViewModel 로직이 참조하는 **모든** Fake 상태가 테스트 본문에 명시적으로 세팅되어 있는가? source map의 해당 항목을 하나씩 대조한다. |
| 2 | **전체 객체 비교** | `assertIs` + 필드 접근, `assertEquals(expected, state.someField)` 패턴이 없는가? |
| 3 | **side-effect 독립** | 하나의 테스트에서 UiState 변경 + UiEvent 발행 + 외부 호출을 동시에 검증하지 않는가? |
| 4 | **날카로운 값** | 초기값과 인자가 검증 대상 로직의 분기를 정확히 드러내는 의미 있는 값인가? |

## 3. 누락 보충 + 위반 수정

누락된 테스트를 테스트 작성 규칙에 따라 추가하고, 기존 테스트의 위반을 수정한다. 테스트를 실행하여 통과를 확인한다.

---

# 작성 모드 (공통 분석 이후)

## 1. 의존성 준비

### Fake 확인 및 작성

`app/src/test/java/com/wafflestudio/snutt2/fake/`에서 기존 Fake를 확인한다. 필요한 Fake가 없으면 새로 작성한다.

Fake는 서버 로직을 모사하지 않는다. 테스트에서 결과를 제어하고 호출을 기록하는 것이 전부다.

```kotlin
class FakeXxxRepository : XxxRepository {
    // (1) StateFlow → MutableStateFlow로 직접 override
    //     private val _xxx + asStateFlow() 패턴을 쓰지 않는다.
    override val user = MutableStateFlow<User?>(null)

    // (2) 결과 제어
    var findIdByEmailResult: Result<Unit> = Result.Success(Unit)

    // (3) 인자 기록 — setter는 반드시 private
    var findIdByEmailCalledWith: String? = null
        private set

    // (4) 호출 여부 (인자 없는 void 메서드)
    var logoutCalled = false
        private set

    // (5) 구현: 기록 + 반환만. StateFlow 갱신 등 부수 동작을 넣지 않는다.
    override suspend fun findIdByEmail(email: String): Result<Unit> {
        findIdByEmailCalledWith = email
        return findIdByEmailResult
    }

    // (6) 이 Fake에서 테스트하지 않는 메서드
    override suspend fun otherMethod() = TODO("Not used in this test")
}
```

**특수 의존성**:
- **DisplayMessageResolver**: `FakeDisplayMessageResolver`가 이미 존재한다. `error.displayMessage`를 그대로 반환한다.
- **UseCase**: concrete 클래스이므로 Fake를 만들지 않는다. UseCase의 Repository 의존성을 Fake로 넣어 실객체를 생성한다.
- **RemoteConfig 등 interface**: Fake를 만든다. Flow 프로퍼티는 MutableStateFlow로 override.

### TestFixtures 확인 및 보완

`app/src/test/java/com/wafflestudio/snutt2/fixture/TestFixtures.kt`를 확인한다. 필요한 도메인 모델이 없으면 팩토리 함수를 추가한다.

```kotlin
object TestFixtures {
    fun searchedLecture(
        id: String = "lec-1",
        courseTitle: String = "컴퓨터개론",
    ) = SearchedLecture(id = id, courseTitle = courseTitle, ...)

    val lecture1 = searchedLecture(id = "lec-1")
}
```

개별 테스트 파일에 fixture를 정의하지 않는다.

## 2. 기대 테스트 목록대로 테스트 작성

기대 테스트 목록의 각 항목을 테스트 작성 규칙에 따라 작성한다.

## 3. 실행 검증

```bash
./gradlew :app:testDevDebugUnitTest --tests "*.XxxViewModelTest"
```

---

# 테스트 작성 규칙

작성 모드에서 새 테스트를 쓸 때, 검증 모드에서 누락 테스트를 보충하거나 위반을 수정할 때 모두 이 규칙을 따른다.

## 테스트 클래스 골격

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class XxxViewModelTest {

    private lateinit var fakeXxxRepository: FakeXxxRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeXxxRepository = FakeXxxRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

모든 의존성은 `@Before`에서 매번 새로 생성한다. `val`로 선언 시점에 초기화하지 않는다.

## ViewModel 생성 시점

| init에 비동기 작업이 | 생성 위치 | 이유 |
|---|---|---|
| **없다** | `@Before`에서 직접 생성 | Fake 세팅 순서 무관 |
| **있다** | `private fun createViewModel()`을 각 테스트에서 호출 | Fake를 먼저 세팅해야 init 결과를 제어 가능 |

### SavedStateHandle

ViewModel이 `savedStateHandle`에서 nav arg를 읽는 경우, `createViewModel()`에서 초기값을 주입한다:

```kotlin
private fun createViewModel(
    themeId: String = "theme-1",
) = ThemeDetailViewModel(
    savedStateHandle = SavedStateHandle(mapOf("themeId" to themeId)),
    themeRepository = fakeThemeRepository,
)
```

## region 구조

나가는 방향은 **public 함수 단위**, 들어오는 방향은 **source 단위**로 region을 묶는다:

```kotlin
// region findIdByEmail
@Test fun `findIdByEmail 호출 시 입력된 이메일로 repository를 호출한다`() = ...
@Test fun `findIdByEmail 성공 시 Success 이벤트가 발생한다`() = ...
@Test fun `findIdByEmail 실패 시 ShowToast 이벤트가 발생한다`() = ...
// endregion

// region source: user
@Test fun `user가 변경되면 userName이 갱신된다`() = ...
// endregion
```

## 검증 원칙: 전체 객체 비교

**UiState, UiEvent 등 data class / sealed class는 전체 객체 단위로만 비교한다.**

개별 필드를 따로 비교하는 것은 모두 금지한다:
- `assertIs<Success>(state)` 후 `state.someField` 검증
- `assertEquals(expected, state.someField)`
- `assertIs<ShowToast>(event)` 후 `event.message` 검증

**패턴 1: 동일 subtype 내 필드 변경 → `before.copy()`**

동작 전 상태를 캡처하고 copy로 기대값을 만든다. copy에 명시된 필드만 바뀌고 나머지는 동일해야 통과하므로, "이 동작이 정확히 무엇을 바꾸는가"를 드러낸다.

```kotlin
val before = viewModel.uiState.value
viewModel.setThemeMode(ThemeMode.LIGHT)
assertEquals(before.copy(themeMode = ThemeMode.LIGHT), viewModel.uiState.value)
```

ContentState 내부 변경 시 nested copy:
```kotlin
val before = viewModel.uiState.value
val beforeContent = before.contentState as ContentState.Loaded
assertEquals(
    before.copy(contentState = beforeContent.copy(field = newValue)),
    viewModel.uiState.value,
)
```

**패턴 2: subtype 전이 → 기대 객체 직접 구성**

Loading → Loaded 등 contentState 자체가 바뀌면 copy할 before가 없다:

```kotlin
assertEquals(
    XxxUiState(contentState = ContentState.Loaded(...)),
    viewModel.uiState.value,
)
```

init combine 결과가 복잡하더라도 기대 객체를 직접 구성한다. "기대 객체가 복잡해질 것 같다"는 이유로 개별 필드 검증으로 후퇴하지 않는다.

**UiEvent 검증**:
```kotlin
viewModel.uiEvent.test {
    viewModel.someAction()
    assertEquals(XxxUiEvent.Success("data"), awaitItem())
}
```

**외부 의존성 호출 검증**:

Fake의 `calledWith` 필드에 기록된 값을 전체 객체 비교로 검증한다.

```kotlin
viewModel.findIdByEmail("test@snu.ac.kr")
assertEquals("test@snu.ac.kr", fakeUserRepository.findIdByEmailCalledWith)
```

인자가 여러 개이면 Pair/Triple 등으로 기록하고 동일하게 전체 비교한다:
```kotlin
viewModel.changePassword("oldPw", "newPw")
assertEquals("oldPw" to "newPw", fakeUserRepository.putUserPasswordCalledWith)
```

## 작성 기준

- **테스트 이름**: 백틱으로 감싸서 한국어로 시나리오 서술. 예: `` `findIdByEmail 성공 시 Success 이벤트가 발생한다` ``
- **하나의 테스트 = 하나의 side-effect × 하나의 분기**: 여러 side-effect를 한 테스트에서 검증하지 않는다.
- **Fake 상태는 테스트 본문에서 세팅**: `@Before`에는 Fake 인스턴스 생성만. 시나리오별 상태는 각 테스트에서.
- **Fake 기본값에 의존하지 않는다**: Fake의 기본값과 같더라도 테스트에 필요한 상태는 명시적으로 세팅한다. 테스트 의도가 Fake 구현을 읽지 않아도 드러나야 한다.
- **헬퍼 함수**: 테스트의 기대값에 영향을 주는 설정을 헬퍼 안에 숨기지 않는다.
- **Given-When-Then**: 복잡한 테스트는 주석으로 구분. 단순한 테스트는 생략 가능.
