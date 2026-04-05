# ViewModel 테스트 전략

## 목표

ViewModel의 비즈니스 로직을 검증한다. 구체적으로:
- 특정 상태에서 public 함수가 호출되었을 때 UiState가 올바르게 전이되는지
- 올바른 UiEvent가 발생하는지
- Repository에 올바른 요청이 전달되는지

## 테스트 대상이 아닌 것

- Repository 내부 로직 (별도 Repository 테스트에서 다룸)
- Compose UI 렌더링
- Navigation 동작
- Hilt DI 바인딩

---

## 의존성 전략: Fake

Mock 대신 Fake를 사용한다. Repository 인터페이스에 대한 간소화된 인메모리 구현체를 직접 작성한다.

### Fake의 역할 범위

Fake는 서버 로직을 모사하지 않는다. 역할은 다음으로 한정한다:
- 성공 시 `Result.Success(미리 정의된 데이터)` 반환
- 실패 시 `Result.Fail(지정된 에러)` 반환
- `StateFlow` 프로퍼티에 대해 값을 세팅하고 노출

### Fake의 구조

```kotlin
class FakeUserRepository : UserRepository {
    // --- StateFlow: MutableStateFlow로 override하여 테스트에서 .value = 로 직접 세팅 ---
    override val user = MutableStateFlow<User?>(null)
    override val themeMode = MutableStateFlow(ThemeMode.AUTO)

    // --- 테스트 제어용 필드 ---
    var findIdByEmailResult: Result<Unit> = Result.Success(Unit)
    var findIdByEmailCalledWith: String? = null
        private set

    // --- 인터페이스 구현 ---
    override suspend fun findIdByEmail(email: String): Result<Unit> {
        findIdByEmailCalledWith = email
        return findIdByEmailResult
    }

    // ... 나머지 메서드도 같은 패턴
}
```

핵심 원칙:
- StateFlow 프로퍼티는 `MutableStateFlow`로 직접 override한다. 테스트에서 `.value = ...`로 source 변화를 시뮬레이션한다. `private val _xxx` + `asStateFlow()` 패턴이나 별도 setter 메서드를 쓰지 않는다.
- 각 suspend 함수마다 `var xxxResult` 필드를 두어 반환값을 테스트에서 제어한다.
- 인자 검증이 필요하면 `var xxxCalledWith` 필드를 두어 마지막 호출의 인자를 기록한다.
- suspend 함수 구현에서는 `calledWith` 기록과 `xxxResult` 반환만 한다. StateFlow 갱신 등 부수 동작을 넣지 않는다.
- 사용하지 않는 메서드는 `TODO("Not used in this test")`로 둔다.

### DisplayMessageResolver

`DisplayMessageResolver`도 인터페이스이므로 Fake를 만든다. 단, 역할이 단순하여 에러의 `displayMessage`를 그대로 반환하는 것으로 충분하다:

```kotlin
class FakeDisplayMessageResolver : DisplayMessageResolver {
    override fun getDisplayTitle(error: DomainError) = error.displayTitle
    override fun getDisplayMessage(error: DomainError) = error.displayMessage
}
```

### UseCase

UseCase는 concrete 클래스이므로 인터페이스 기반 Fake를 만들지 않는다. UseCase의 의존성(Repository)을 Fake로 넣어 실객체를 생성한다:

```kotlin
val useCase = GetCurrentTableThemeUseCase(
    themeRepository = fakeThemeRepository,
    tableRepository = fakeTableRepository,
)
```

### RemoteConfig 등 interface 의존성

RemoteConfig처럼 Repository가 아닌 interface 의존성도 Fake를 만든다. Flow 프로퍼티는 `MutableStateFlow`로 override한다:

```kotlin
class FakeRemoteConfig : RemoteConfig {
    override val sugangSNUUrl = MutableStateFlow("https://sugang.snu.ac.kr")
    override val disableMapFeature = MutableStateFlow(false)
    // ...
}
```

### Fake 파일 위치

```
app/src/test/java/com/wafflestudio/snutt2/
├── fake/                          # Fake 구현체
│   ├── FakeUserRepository.kt
│   ├── FakeBookmarkRepository.kt
│   ├── FakeDisplayMessageResolver.kt
│   └── ...
├── fixture/                       # 공유 테스트 데이터
│   └── TestFixtures.kt
└── views/                         # 실제 ViewModel 경로를 미러링
    └── logged_out/
        └── FindIdViewModelTest.kt
```

- Fake는 `fake/` 패키지에 모아 둔다. 여러 ViewModel 테스트에서 공유된다.
- 테스트 파일은 대상 ViewModel과 동일한 패키지 경로에 둔다.

### 테스트 픽스처 (TestFixtures)

테스트에서 사용하는 도메인 모델 인스턴스는 `fixture/TestFixtures.kt`에 모아 둔다. 각 테스트 파일의 companion object에 개별 정의하지 않는다.

```kotlin
object TestFixtures {
    // 팩토리 함수: 테스트에서 필요한 필드만 지정, 나머지는 기본값
    fun searchedLecture(
        id: String = "lec-1",
        courseTitle: String = "컴퓨터개론",
        ...
    ) = SearchedLecture(id = id, courseTitle = courseTitle, ...)

    // 자주 쓰이는 인스턴스는 val로 미리 정의
    val lecture1 = searchedLecture(id = "lec-1", courseTitle = "컴퓨터개론")
    val lecture2 = searchedLecture(id = "lec-2", courseTitle = "자료구조")
}
```

- 도메인 모델마다 **팩토리 함수**를 둔다. 필수 필드만 파라미터로 노출하고 나머지는 기본값을 채운다.
- 여러 테스트에서 반복 사용되는 인스턴스는 `val`로 미리 정의한다.
- 테스트에서는 `TestFixtures.lecture1` 또는 `import ... TestFixtures.lecture1`로 사용한다.
- 특정 테스트에서만 필요한 특수 데이터는 팩토리 함수에 인자를 넘겨 생성한다.

---

## 테스트 코드 구조

### 기본 골격

```kotlin
class FindIdViewModelTest {

    private lateinit var fakeUserRepository: FakeUserRepository
    private lateinit var fakeDisplayMessageResolver: FakeDisplayMessageResolver
    private lateinit var viewModel: FindIdViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeUserRepository = FakeUserRepository()
        fakeDisplayMessageResolver = FakeDisplayMessageResolver()
        viewModel = FindIdViewModel(
            userRepository = fakeUserRepository,
            displayMessageResolver = fakeDisplayMessageResolver,
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}
```

Fake를 포함한 모든 의존성은 `@Before`에서 매번 새로 생성한다. `val`로 필드 선언 시점에 초기화하지 않는다. JUnit 4는 테스트마다 클래스를 재생성하므로 동작은 동일하지만, `lateinit` + `@Before` 패턴이 "매 테스트마다 초기화됨"을 명시적으로 드러낸다.

### ViewModel 생성: `@Before` vs `createViewModel()`

init에서 비동기 작업을 수행하는 ViewModel은 Fake 상태를 **ViewModel 생성 전에** 세팅해야 한다. 이 경우 `@Before`에서 ViewModel을 생성하면 Fake 세팅 시점을 제어할 수 없으므로, `private fun createViewModel()` 팩토리 메서드를 사용한다:

```kotlin
class VacancyViewModelTest {

    private lateinit var fakeVacancyRepository: FakeVacancyRepository

    @Before
    fun setup() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        fakeVacancyRepository = FakeVacancyRepository()
        // ViewModel은 여기서 생성하지 않는다
    }

    private fun createViewModel() = VacancyViewModel(
        vacancyRepository = fakeVacancyRepository,
        // ...
    )

    @Test
    fun `init 시 fetch 성공하면 Loaded 상태가 된다`() = runTest {
        // Fake 세팅을 먼저 한 뒤
        fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
        fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)
        // ViewModel 생성 (init 실행)
        val viewModel = createViewModel()
        assertEquals(...)
    }
}
```

- init에 비동기 작업이 **없는** ViewModel → `@Before`에서 직접 생성
- init에 비동기 작업이 **있는** ViewModel → `createViewModel()` 팩토리 사용

### 검증 범주

ViewModel 테스트는 **나가는 방향**과 **들어오는 방향** 두 축으로 나뉜다.

**나가는 방향**: public 함수 호출 시 발생하는 효과

| 범주 | 검증 내용 | 해당되지 않는 경우 |
|------|-----------|-------------------|
| **Repository 호출** | 올바른 인자로 호출되었는지 | - |
| **UiEvent 발행** | 성공/실패 시 올바른 이벤트가 발행되는지 | UiEvent가 없는 함수 |
| **UiState 전이** | public 함수 호출 후 상태가 올바르게 변경되는지 | UiState가 없는 ViewModel |

**들어오는 방향**: ViewModel이 collect하는 source의 변화에 대한 반응

| 범주 | 검증 내용 |
|------|-----------|
| **Source 반응** | collect 대상 StateFlow 변화 시 UiState가 올바르게 반영되는지 |

source 테스트에서는 Fake의 `MutableStateFlow`에 `.value = ...`로 변화를 주고, UiState를 검증한다.

하나의 테스트에서 여러 범주를 동시에 검증하지 않는다.

#### 테스트 작성 절차

**나가는 방향**: 각 public 함수에 대해, 아래 범주를 **빠짐없이 열거**하고 해당되는 것마다 테스트를 작성한다:

1. **Repository 호출**: 올바른 인자로 호출되었는가?
2. **UiEvent (성공)**: 성공 시 올바른 이벤트가 발행되는가?
3. **UiEvent (실패)**: 실패 시 올바른 에러 이벤트가 발행되는가?
4. **UiState 전이**: 상태가 올바르게 변경되는가?

해당되지 않는 범주는 건너뛴다 (예: Repository를 호출하지 않는 함수는 1번 생략). 하지만 **해당되는데 빠뜨리지 않도록** 매 함수마다 위 목록을 체크한다.

**들어오는 방향**: 테스트 작성 전에 init의 combine/collect 로직을 읽고, **각 source가 UiState의 어떤 필드에 어떤 변환으로 기여하는지** 파악한다. 그 이해를 바탕으로 시나리오를 도출한다.

1. init 블록의 combine/collect 대상 source를 열거한다.
2. 각 source가 combine 내에서 **어떤 로직에 사용되는지** 파악한다 (그룹핑, 정렬, 조건부 플래그, 상태 보존 등).
3. 그 로직에서 의미 있는 시나리오를 도출하여 테스트를 작성한다.

예: `combine(courseBooks, tableSummaryList, currentTable)`에서
- `courseBooks.first()`가 최신 학기를 결정 → 최신 학기에 시간표가 없으면 빈 항목 + dot 표시
- `tableSummaryList`가 courseBook별로 그룹핑 → 시간표 추가 시 해당 학기 tableList에 반영
- `currentTable`의 courseBook이 expanded 결정 → 학기 전환 시 expanded 이동
- `previousExpandedState`가 이전 expanded를 보존 → source 변화 후에도 수동 펼침 유지

"source가 변하면 UiState가 갱신된다" 같은 피상적 검증이 아니라, **combine 로직의 구체적인 동작**을 검증해야 한다.

나가는 방향 테스트는 **public 함수 단위로**, 들어오는 방향 테스트는 **source 단위로** region을 묶어 구조화한다:

```kotlin
// region findIdByEmail

@Test
fun `findIdByEmail 호출 시 입력된 이메일로 repository를 호출한다`() = runTest {
    viewModel.findIdByEmail("test@snu.ac.kr")
    assertEquals("test@snu.ac.kr", fakeUserRepository.findIdByEmailCalledWith)
}

@Test
fun `findIdByEmail 성공 시 Success 이벤트가 발생한다`() = runTest {
    fakeUserRepository.findIdByEmailResult = Result.Success(Unit)

    viewModel.uiEvent.test {
        viewModel.findIdByEmail("test@snu.ac.kr")
        assertEquals(FindIdUiEvent.Success("test@snu.ac.kr"), awaitItem())
    }
}

@Test
fun `findIdByEmail 실패 시 ShowToast 이벤트가 발생한다`() = runTest {
    val error = Unknown(displayTitle = "", displayMessage = "에러 발생")
    fakeUserRepository.findIdByEmailResult = Result.Fail(error)

    viewModel.uiEvent.test {
        viewModel.findIdByEmail("test@snu.ac.kr")
        val event = assertIs<FindIdUiEvent.ShowToast>(awaitItem())
        assertEquals("에러 발생", event.message)
    }
}

// endregion
```

### UiState 검증

UiState는 **전체 객체를 비교**한다. 개별 필드를 하나씩 `assertEquals` 하지 않는다. 특정 필드만 검증하면 의도치 않은 다른 필드의 변경을 놓칠 수 있다.

검증 패턴은 두 가지 상황에 따라 나뉜다.

#### 패턴 1: 동일 subtype 내 필드 변경 — `before.copy()`

동작 직전의 상태를 캡처해두고, `copy`로 기대값을 만든다:

```kotlin
@Test
fun `setThemeMode 호출 시 themeMode가 변경된다`() = runTest {
    val before = viewModel.uiState.value

    viewModel.setThemeMode(ThemeMode.LIGHT)

    assertEquals(before.copy(themeMode = ThemeMode.LIGHT), viewModel.uiState.value)
}
```

이 패턴의 장점:
- `copy`에 명시된 필드만 바뀌고, 나머지는 동일해야 테스트가 통과한다.
- 테스트가 "이 동작이 정확히 무엇을 바꾸는가"를 명확히 드러낸다.
- Fake 세팅이나 init 로직이 변경되어도, 실제 상태를 기준으로 하므로 테스트가 불필요하게 깨지지 않는다.

ContentState + data class 구조에서도 동일하다. `contentState` 내부를 변경할 때는 캐스팅 후 nested `copy`한다:

```kotlin
@Test
fun `toggleLectureSelected 호출 시 해당 강의가 선택된다`() = runTest {
    val before = viewModel.vacancyUiState.value
    val beforeContent = before.contentState as VacancyUiState.ContentState.Loaded

    viewModel.toggleLectureSelected(lecture1.id)

    assertEquals(
        before.copy(
            contentState = beforeContent.copy(
                vacancyLecturesWithSelection = beforeContent.vacancyLecturesWithSelection.map {
                    if (it.item.id == lecture1.id) it.copy(state = true) else it
                },
                deleteButtonEnabled = true,
            ),
        ),
        viewModel.vacancyUiState.value,
    )
}
```

#### 패턴 2: contentState 전이 — 기대 객체 직접 구성

Loading → Loaded 등 **contentState 자체가 바뀌는 경우**에는 `copy`할 "before"가 없다. 이때는 기대하는 전체 객체를 직접 구성하여 비교한다:

```kotlin
@Test
fun `init 시 fetch 성공하고 강의가 있으면 Loaded 상태가 된다`() = runTest {
    fakeVacancyRepository.fetchVacancyLecturesResult = Result.Success(Unit)
    fakeVacancyRepository.vacancyLectures.value = listOf(lecture1)

    val viewModel = createViewModel()

    assertEquals(
        VacancyUiState(
            contentState = VacancyUiState.ContentState.Loaded(
                vacancyLecturesWithSelection = listOf(lecture1.toDataWithState(false)),
            ),
        ),
        viewModel.vacancyUiState.value,
    )
}
```

**어떤 경우든 개별 필드를 하나씩 assertEquals 하지 않는다.** 반드시 전체 객체 단위로 비교한다.

#### 주의: init combine 결과도 예외 없이 전체 객체 비교

init의 combine이 복잡한 파생 로직(그룹핑, 정렬, 조건부 플래그 등)으로 상태를 구성하더라도 기대 객체를 직접 만들어 비교한다. "기대 객체가 복잡해질 것 같다"는 이유로 개별 필드 검증으로 후퇴하지 않는다.

실제로 기대 객체를 구성해 보면 대부분 가능하고, 이를 통해 combine 로직의 모든 출력 필드가 한눈에 검증된다:

```kotlin
@Test
fun `init 시 courseBooks와 tableSummaryList로 서랍 목록이 구성된다`() = runTest {
    // ... Fake 세팅 ...
    val viewModel = createViewModel()

    assertEquals(
        HomeDrawerUiState(
            courseBookDrawerItemList = listOf(
                CoursebookDrawerItem(
                    courseBook = courseBook2025_1,
                    showNewCoursebookDot = false,
                    tableList = listOf(summary1),
                ).toDataWithState(true),
                CoursebookDrawerItem(
                    courseBook = courseBook2024_2,
                    showNewCoursebookDot = false,
                    tableList = listOf(summary2),
                ).toDataWithState(false),
            ),
            selectedTable = summary1,
        ),
        viewModel.uiState.value,
    )
}
```

---

## 필요한 라이브러리

```toml
# gradle/libs.versions.toml
[versions]
turbine = "1.2.0"
coroutines-test = "1.9.0"  # 프로젝트의 코루틴 버전과 맞출 것

[libraries]
turbine = { module = "app.cash.turbine:turbine", version.ref = "turbine" }
kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "coroutines-test" }
```

```kotlin
// app/build.gradle.kts
testImplementation(libs.junit)
testImplementation(libs.kotlin.test)
testImplementation(libs.kotlinx.coroutines.test)
testImplementation(libs.turbine)
```

Mock 라이브러리(MockK, Mockito 등)는 사용하지 않는다.

---

## 테스트 작성 순서

복잡도 순으로 단계적으로 확장한다:

| 단계 | 대상 | 새로 다루는 패턴 | 상태 |
|------|------|-----------------|------|
| 1 | FindIdViewModel | UiEvent, 성공/실패 분기 | 완료 |
| 2 | ColorModeSelectViewModel | UiState, init Flow collect | 완료 |
| 3 | AppReportViewModel | 초기값 + UiEvent (복합) | 완료 |
| 4 | VacancyViewModel | sealed ContentState, B-1 init 비동기, 다이얼로그 상태 전이 | 완료 |
| 5 | BookmarkViewModel 등 | 복합 상태, 바텀시트, 다중 Repository | |

---

## 작성 기준

- **테스트 이름**: 백틱(`` ` ``)으로 감싸서 한국어로 시나리오를 서술한다.
  - 예: `` `findIdByEmail 성공 시 Success 이벤트가 발생한다` ``
- **Given-When-Then**: 복잡한 테스트는 주석으로 단계를 구분한다. 단순한 테스트는 생략 가능.
- **하나의 테스트에 하나의 검증**: 한 테스트에서 여러 시나리오를 검증하지 않는다.
- **Fake 상태 설정은 테스트 안에서**: `@Before`에는 최소한의 공통 설정만 두고, 시나리오별 Fake 상태는 각 테스트에서 설정한다.
- **Fake의 초기값에 의존하지 않는다**: 테스트에 필요한 상태는 Fake의 기본값과 같더라도 명시적으로 세팅한다. 테스트의 의도가 Fake 구현을 읽지 않아도 드러나야 한다.
