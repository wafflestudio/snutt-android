# 패키지 스캐폴딩

## 목적

이 문서는 `com.wafflestudio.snutt2` 패키지의 폴더 구조와 각 패키지의 의미,
그리고 위치가 결정될 때 논의가 있었던 부분의 근거를 정리한다.

새 파일을 어디에 둘지 고민될 때, 기존 패키지의 성격을 맞추는 판단 근거로 사용한다.

---

## 전체 구조

```
com.wafflestudio.snutt2/
├── SNUTTApplication.kt              앱 엔트리
├── RootActivity.kt                  앱 엔트리
├── SNUTTFirebaseMessagingService.kt FCM 서비스 (Manifest 등록)
├── TimetableWidgetProvider.kt       홈 위젯 (Manifest 등록)
│
├── feature/                         기능 단위 화면 (Route + Screen + ViewModel + 기능 전용 컴포넌트)
│   ├── bookmark/
│   ├── debug/
│   ├── diary/
│   ├── friend/
│   ├── home/                        드로어/타임테이블/팝업 등 홈에 내장된 요소는 home/ 하위에 유지
│   ├── lecture_detail/
│   ├── login/                       로그인/회원가입/튜토리얼/비번찾기
│   ├── notifications/
│   ├── reviews/
│   ├── search/
│   ├── settings/
│   ├── table_lectures/
│   ├── theme_config/
│   ├── thememarket/
│   └── vacancy_noti/
│
├── ui/                              UI 레이어의 공용 부품 (feature에 속하지 않는 것)
│   ├── components/                  공용 Compose 컴포넌트
│   │   └── view/                    Compose가 못 쓰이는 컨텍스트(위젯/스크린샷)용 legacy Android View
│   ├── theme/                       Colors, Theme, Typo, ThemeMode, LocalThemeState
│   └── util/                        UI 작성 헬퍼 (dp/sp 변환, 클립보드 등 도메인 무관 헬퍼)
│       └── formatter/               도메인 모델 → 표시 문자열/색 변환 (Context/리소스 의존)
│
├── navigation/                      앱 라우팅 구조
│                                    RootNavGraph, NavigationDestination, NavigationResult,
│                                    DeepLinkPath, DeepLinkUtil, DeeplinkParser, LectureColorNavType
│
├── config/                          앱 런타임 설정
│                                    RemoteConfig, FeatureFlag
│
├── domain/                          도메인 레이어
│   ├── model/                       도메인 모델
│   └── (루트)                       UseCase, DomainService, DomainError, DisplayMessageResolver
│
├── data/                            Repository 레이어 (data source 추상화)
│                                    기능별 폴더 + mapper/
│
├── network/                         Data source: 서버 API
│                                    api/, dto/, error/
│
├── storage/                         Data source: 로컬 저장소 (SharedPreference 기반)
│                                    루트 + model/, pref/
│
├── di/                              Hilt 모듈
├── logging/                         Analytics (AnalyticsLogger, AnalyticsEvent, compose/)
│
└── lib/                             횡단 인프라 (레이어나 feature에 속하지 않는 코드)
    ├── (루트)                       순수 Kotlin 유틸 (FlowExt, DebouncePerKey, DataWithStateUtil)
    ├── android/                     Android 플랫폼 래퍼 (NetworkConnectivityManager, NetworkLogger,
    │                                DisplayMessageResolverImpl, webview/)
    ├── facebook/                    Facebook SDK 래퍼 (3자 의존성 격리)
    └── serializer/                  Moshi 기반 JSON 직렬화 (storage와 network 양쪽에서 사용)
```

---

## 각 최상위 패키지의 의미

### 패키지 루트 직속 파일

`SNUTTApplication`, `RootActivity`, `SNUTTFirebaseMessagingService`, `TimetableWidgetProvider`.
모두 **AndroidManifest에 등록되는 앱의 진입점(entry point)**이다. 하위 패키지로 분류하지 않고 루트에 둔다.

### `feature/`

각 기능의 화면을 담는다. 한 feature 폴더 안에는 그 화면의 Route, Screen, ViewModel, UiState,
UiEvent, 그리고 오직 그 feature에서만 사용하는 컴포넌트/유틸이 들어간다.

### `ui/`

UI 레이어에서 여러 feature가 공용으로 사용하는 부품이 들어간다.

- `components/` — 공용 Compose 컴포넌트
  - `view/` — Compose가 쓰일 수 없는 컨텍스트(위젯, 스크린샷 캡처 등)에서 호출되는 legacy Android View 격리 구역
- `theme/` — 색상, 타이포, `ThemeMode` enum, `LocalThemeState` CompositionLocal
- `util/` — UI 작성에 쓰이는 순수 헬퍼 (dp 변환, 클립보드, Toast 등 도메인 무관)
  - `formatter/` — 도메인 모델 → 표시 문자열/색 변환 (Context, R.string, Compose Color 등에 의존)

### `navigation/`

`RootNavGraph`와 각 feature의 `NavigationDestination`, 딥링크 경로 정의/파싱, 커스텀 NavType 등.
앱 전체의 화면 연결 구조를 담는 인프라.

### `config/`

`RemoteConfig` (서버에서 내려오는 런타임 설정)와 `FeatureFlag` (컴파일 타임 feature toggle).
둘 다 "앱이 어떤 설정으로 동작하는가"를 결정하는 공통 성격.

### `domain/`

도메인 레이어. `domain/model/` 하위에 도메인 모델, 루트에 UseCase / DomainService / DomainError 등
도메인 로직이 위치한다.

### `data/`

Repository 레이어. 기능별 폴더 안에 `XxxRepository` 인터페이스와 `XxxRepositoryImpl` 구현이 함께
들어간다. DTO → 도메인 모델 변환은 `data/mapper/`.

### `network/`, `storage/`

`Repository`가 의존하는 data source 레이어. 각각 서버 API와 로컬 저장소.
`data/`와 별개 최상위 패키지로 둔다 (아래 설계 결정 참조).

### `di/`

Hilt 모듈. 모든 레이어를 참조하므로 특정 레이어 안에 둘 수 없어 최상위 유지.

### `logging/`

Analytics. UI/Data 등 여러 레이어를 횡단하는 관심사이므로 독립 패키지.

### `lib/`

**레이어나 feature에 속하지 않는 횡단 인프라 유틸리티.**

- 루트: 순수 Kotlin 유틸 (Android 비의존)
- `android/`: Android 플랫폼을 감싸는 횡단 인프라 (DI 주입되거나 OkHttp 인터셉터처럼 비-UI에서도 동작)
- `facebook/`: 3자 SDK(Facebook) 의존성 격리 구역
- `serializer/`: Moshi 기반 JSON 직렬화. `storage`와 `network` 양쪽에서 쓰므로 어느 한쪽에 귀속할 수 없음

---

## 주요 설계 결정

### `views/` → `feature/`로 개명, 평탄화

이전에는 `views/logged_in/home/settings/theme/...` 처럼 네비게이션 계층을 폴더 구조로 표현했다.
이 계층은 `RootNavGraph`에 이미 표현되어 있어 폴더로 중복 표현할 필요가 없고, 파일을 찾을 때마다
"이 화면이 어디 아래 있더라?"를 기억해야 하는 비용이 있었다.

현재는 `feature/` 한 단계 평탄화. 예외는 `feature/home/` 내부(drawer, timetable, popups) — 이들은
홈 화면에 **물리적으로 내장된 컴포넌트**이지 독립 화면이 아니라서 그대로 둔다.

이름은 `views/`가 ViewModel까지 포함하므로 부정확하고, `ui/`는 이미 공용 부품으로 쓰이므로,
`feature/`가 적절하다.

### `ui/`는 공용 부품만, feature는 `feature/`로 분리

`ui/`와 `feature/`를 합쳐서 한 패키지에 두는 안(`ui/search/`, `ui/home/`, `ui/components/`)도
고려했으나, ABC 정렬 시 인프라(`components`, `theme`, `util`)가 기능 폴더(`bookmark`, `diary`, ...)
사이에 섞여 시각적으로 구분이 어려워진다. 또한 ViewModel을 포함하는 것을 `ui/`라 부르는 건
부정확하다. 그래서 둘을 다른 최상위 패키지로 분리.

### `ui/util/` 안의 `formatter/` 분리

이전 `ui/util/`은 두 성격이 섞여 있었다:

1. **도메인 모델 → 표시 변환** — `Lecture` 포맷터, `CourseBook.toFormattedString`, `TagType.color()` 등 Context/R.string/Compose Color에 의존
2. **순수 UI/플랫폼 헬퍼** — `dp/sp` 변환, `Context.toast`, 클립보드 등 도메인 무관

새 파일을 둘 때 어느 쪽인지 모호해서, `formatter/` 서브패키지로 1번을 분리. `ui/util/` 루트는 도메인 의존이 없는 순수 UI 헬퍼만 남는다.

판단 기준:

- 도메인 모델을 receiver/인자로 받아 표시 문자열·색을 만드는 함수 → `formatter/`
- Context/Compose 단순 래퍼이고 도메인 무관 → `ui/util/` 루트

### 도메인 모델 확장은 목적으로 분류

도메인 모델(`Lecture`, `LectureSession` 등)에 대한 확장 함수는 두 종류로 나뉜다:

1. **순수 도메인 로직** — `LectureSession.trimByTrimParam`, `Lecture.contains`, `List<Lecture>.getFittingTrimParam` 등. UI 의존 0, ViewModel/Data 어느 레이어에서도 호출 가능 → **`domain/model/*Ext.kt`**
2. **표시 변환** — `CourseBook.toFormattedString(Context)`, `TagType.color()` 등. Context/Compose에 의존 → **`ui/util/formatter/`**

"도메인 모델 확장이니까 다 같은 곳"이 아니라, **확장의 목적이 도메인 로직인지 UI 표현인지**로 분류한다.

### `lib/` 분류 기준은 "사용처와 목적"

`lib/`는 "**레이어나 feature에 속하지 않는 횡단 인프라**"이다. "순수 Kotlin이라서 lib"이 기준이 되면, 사용처가 한 feature뿐인 함수도 lib에 가버려서 정의가 무너진다.

따라서 순수 Kotlin 함수의 분류는:

- **표시 목적** → UI 비의존이라도 `ui/util/formatter/` (예: `LocalTime.getHourMinuteString()`)
- **단일 feature 전용** → 그 feature 패키지로
- **여러 feature/레이어 횡단** → `lib/` 루트 (예: `ValidationExt`, `LocalDateTimeExt`, `MathUtil`)

### `ui/components/view/`는 non-Compose 격리 구역

`TimetableView`는 Compose가 아닌 legacy Android `View`다. 사용처는 두 곳:

- `TimetableWidgetProvider` (홈 위젯) — RemoteViews 컨텍스트에서 Compose 사용 불가
- `feature/home/drawer/ScreenshotUtil` — Canvas 캡처 기반 시간표 공유

두 곳 모두 "Compose가 쓰일 수 없는 컨텍스트에서 시간표를 시각화"하는 케이스다. 일반 화면 시간표는 Compose(`feature/home/timetable/`)로 그린다.

`ui/components/`는 본래 공용 Compose 컴포넌트 모음이지만, 위 두 사용처를 위해 `view/` 서브폴더에 legacy View 클래스(`TimetableView`, `TextRect`)를 격리한다. 새 Android View가 추가되면 같은 폴더에 모은다.

### `navigation/`은 `ui/` 밖, 최상위 유지

"`navigation/`도 UI 레이어이니 `ui/navigation/`에 넣자"는 안이 있었으나 기각.

근거:

- `ui/components/`, `ui/theme/`, `ui/util/`은 **"화면을 어떻게 그리는가"** (렌더링 관심사)이고,
  `navigation/`은 **"화면을 어떻게 연결하는가"** (앱 구조 관심사)로 성격이 다르다.
- `navigation/`, `di/`, `config/`는 모두 **"앱을 어떻게 조립하는가"** 라는 직교 관심사.
  `navigation/`만 `ui/` 안으로 옮기면 이 카테고리의 일관성이 깨진다.
- `RootNavGraph`가 모든 feature를 참조하는 최상위 조립 지점인데, 이를 `ui/` 서브패키지로 두면
  "ui의 일부가 feature 전체를 안다"는 위계 역전이 생긴다.

### `network/`, `storage/`는 `data/` 밖, 최상위 유지

CLAUDE.md의 아키텍처 정의상 `Repository` (data layer)와 `DataSource` (network/storage)는
**별개 레이어**이다. `data/` 안에 `network/`, `storage/`를 넣으면 폴더 구조에서 이 레이어 구분이
사라지고, Repository와 DataSource가 같은 레벨에 놓인다.

또한 `network/`는 API/DTO/Error까지 합쳐 133파일 규모로 독립 패키지의 체급이 충분하다.

### `domainmodel/` → `domain/model/`

이전 `domain/` (UseCase 등)과 `domainmodel/` (도메인 모델)은 **같은 도메인 레이어의 두 측면**이므로
굳이 최상위 패키지 2개로 분리할 이유가 없다. `domain/model/` 서브패키지로 통합.

### `config/` 신설

`RemoteConfig.kt`와 `FeatureFlag.kt`는 이전에 각각 패키지 루트, `lib/featureflag/`에 있었다.
둘 다 "앱 런타임 설정"이라는 같은 성격이므로 `config/` 하나로 모음.

### 1-2 파일짜리 최상위 패키지 정리

이전에는 `kakao/`(2), `provider/`(1), `service/`(1), `debug/`(4) 등이 최상위에 있었다.
"파일이 적으니 최상위에 둘 이유가 없다"가 아니라, **각각의 목적지가 이미 다른 곳에 있었다:**

| 이전                                         | 현재                | 근거                                 |
|--------------------------------------------|-------------------|------------------------------------|
| `provider/TimetableWidgetProvider.kt`      | 패키지 루트            | Android 진입점, `RootActivity`와 같은 성격 |
| `service/SNUTTFirebaseMessagingService.kt` | 패키지 루트            | Android 진입점                        |
| `kakao/`                                   | `feature/friend/` | 유일 사용처가 친구 추가 카카오 공유               |
| `debug/` (TestRoute, TestViewModel 등)      | `feature/debug/`  | Route + ViewModel을 가진 미니 feature   |

### `lib/`에서 feature 전용 유틸 분리

`lib/`는 "횡단 인프라"여야 하는데, 실제로는 특정 feature에서만 쓰이는 유틸이 섞여 있었다.
사용처가 하나뿐인 것들은 해당 feature 안으로 이동했다.

| 이전                                                   | 현재                     | 유일 사용처                       |
|------------------------------------------------------|------------------------|------------------------------|
| `lib/ScreenshotUtil.kt`                              | `feature/home/drawer/` | HomeDrawerViewModel (시간표 공유) |
| `lib/KakaoShareMessage.kt`, `KakaoTemplates.kt`      | `feature/friend/`      | FriendsPage2 (친구 추가 공유)      |
| `lib/android/webview/ReviewWebViewContainer.kt`      | `feature/reviews/`     | reviews 전용                   |
| `lib/android/webview/ThemeMarketWebViewContainer.kt` | `feature/thememarket/` | thememarket 전용               |

`WebViewContainer` 인터페이스와 `LoadState`는 양쪽 feature에서 공유하므로 `lib/android/webview/`에
남았다. "`ui/components/webview/`에 넣자"는 안도 있었으나, `ui/components/`는 Compose 컴포넌트
모음이라 Android 플랫폼 종속 인터페이스가 들어가기엔 이질적이어서 기각.

### `lib/` 내부 구분의 기준

| 위치                | 기준                                                       |
|-------------------|----------------------------------------------------------|
| `lib/` 루트         | 순수 Kotlin, Android 독립 (FlowExt 등)                        |
| `lib/android/`    | Android 플랫폼을 감싼 횡단 인프라. DI로 주입되거나 인터셉터처럼 비-UI에서도 동작      |
| `lib/facebook/`   | 3자 SDK 의존성 격리 구역. `com.facebook.*` import는 이 폴더에만 존재해야 함 |
| `lib/serializer/` | 크로스-레이어 직렬화                                              |

`lib/android/` vs `ui/util/` 경계: **"DI로 주입되거나 비-UI 코드에서도 동작"** 하면 `lib/android/`,
**"UI 코드 작성을 돕는 확장 함수/헬퍼"** 이면 `ui/util/`.

예: `NetworkConnectivityManager`는 DI로 주입되므로 `lib/android/`, `ClipboardUtil.copyToClipboard`는
UI에서 호출되는 Context 확장이므로 `ui/util/`.

### `DisplayMessageResolverImpl`의 위치

`domain/DisplayMessageResolver`의 구현체지만 Android Context를 사용한다. `data/` 레이어에
넣는 안도 가능했으나, 이 클래스는 Repository가 아니라 **에러 메시지를 리소스 문자열로 변환하는
프레젠테이션성 로직**이다. 그리고 Context 의존으로 data 레이어의 순수성을 헤치는 것도 꺼려졌다.

결과적으로 `lib/android/`에 두기로 했다 — "Android Context를 쓰고 DI로 주입되는 횡단 인프라"
라는 일관된 기준에 들어맞는다.

### `lib/serializer/`가 `storage/`나 `network/`에 들어가지 않은 이유

`Serializer`, `MoshiSerializer`는 `storage/pref/PrefStorageImpl` (JSON 저장)과 `network/error`
(에러 응답 파싱) **양쪽에서 모두 사용**된다. 어느 한쪽 패키지에 귀속하면 다른 쪽이 반대 방향으로
의존하게 되어 부자연스럽다. 그래서 중립 위치인 `lib/serializer/`에 둔다.

---

## 남은 정리 작업

### `lib/` 내부 정리

전반적으로 재정리 필요

### `ui/util/` 파일명 규칙 통일

`*Util.kt`, `*Ext.kt`, `SNUTT*.kt` 혼재 — 일관된 컨벤션으로 정리.
