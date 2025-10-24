## **기본 구조**

안드로이드 권장 아키텍처를 따른다.

`View - ViewModel - Repository - Data source`

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

**UiState**

의미

- 각 Route 별 UI의 모든 상태를 나타내는 단일 인터페이스

역할

- 각 Route가 가질 수 있는 상태를 적절히 분류해서 sealed interface 로 표현
    - 예: Success / Loading / Empty / Error
    - 하나밖에 없다면 data class 로 바로 표시
- 각 개별 하위 상태별 UI를 그리기 위한 모든 값을 필드로 표현

사용

- 인스턴스 생성은 오직 ViewModel이 담당
- ViewModel 은 StateFlow<UiState> 를 UI에게 제공
    - StateFlow 의 값 변경 또한 오직 ViewModel이 담당 (필드에 var 절대 없음)

---

### **Data 레이어**

**Repository**

역할

- data source 로의 요청 및 data source 로부터의 데이터 수신을 추상화
- (SNUTTStorage 한정) SNUTTStorage의 StateFlow 를 단순 전달

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