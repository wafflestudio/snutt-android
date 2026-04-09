# 패키지 스캐폴딩 리팩토링 계획

## 목표 구조

```
snutt2/
├── SNUTTApplication.kt         앱 진입점
├── RootActivity.kt             앱 진입점
├── RemoteConfig.kt
│
├── feature/                    기능 화면 (Route + Screen + ViewModel)
│   ├── home/                   홈 화면 + 내장 하위요소 (drawer, timetable, popups)
│   ├── bookmark/
│   ├── search/
│   ├── settings/
│   ├── diary/
│   ├── lecture_detail/
│   ├── notifications/
│   ├── vacancy_noti/
│   ├── thememarket/
│   ├── friend/
│   ├── review/
│   ├── login/                  로그인/회원가입/튜토리얼 등 (현 logged_out)
│   └── table_lectures/
│
├── ui/                         UI 렌더링 공용 부품
│   ├── components/             공유 컴포저블 (현 components/)
│   └── theme/                  Compose 테마 — Colors, Theme, Typo (현 ui/)
│
├── navigation/                 앱 라우팅 구조
│                               RootNavGraph, NavigationDestination, NavigationResult,
│                               DeepLinkPath, DeepLinkUtil, DeeplinkParser, LectureColorNavType
│
├── domain/                     도메인
│   ├── model/                  도메인 모델 (현 domainmodel/)
│   └── (UseCase, DomainService 등은 루트에)
│
├── data/                       Repository (현행 유지)
├── network/                    서버 API — api, dto, error (현행 유지)
├── storage/                    로컬 저장소 (현행 유지)
├── di/                         DI 모듈 (현행 유지)
├── logging/                    Analytics (현행 유지)
├── lib/                        유틸리티 (현행 유지, 후속 정리)
├── kakao/                      카카오 공유 (현행 유지)
├── provider/                   위젯 (현행 유지)
└── service/                    FCM (현행 유지)
```

## 설계 근거

### 최상위 패키지 원칙

각 최상위 패키지는 독립적인 아키텍처 관심사를 대표한다.

- `feature/`, `ui/`, `navigation/` — UI 레이어 (기능 / 렌더링 부품 / 라우팅으로 분리)
- `domain/` — 도메인 레이어
- `data/` — Repository 레이어
- `network/`, `storage/` — DataSource 레이어 (Repository와 별개 레이어이므로 data/ 밖에 독립)
- `di/`, `logging/`, `lib/` — 횡단 관심사

### 주요 결정

| 결정 | 근거 |
|------|------|
| `views/` → `feature/` | ViewModel 포함하므로 `ui/`는 부정확. `feature/`가 Route+Screen+ViewModel 단위를 정확히 표현 |
| `feature/` 내부 평탄화 | 네비게이션 계층은 코드(NavGraph)에 이미 표현됨. 폴더로 중복 표현하면 탐색 비용만 증가 |
| `home/`만 하위 구조 유지 | drawer, timetable, popups는 홈 화면에 물리적으로 내장된 컴포넌트. 독립 기능이 아님 |
| `ui/`는 렌더링 부품만 | theme + components. navigation은 "어떻게 그리나"가 아니라 "어떻게 연결하나"이므로 별도 |
| `navigation/` 최상위 | 앱 구조 인프라. di/와 같은 위계. service/, provider/에서 참조 가능성 |
| `domain/` + `domainmodel/` 통합 | 같은 도메인 레이어의 두 측면. model/을 하위 패키지로 |
| `network/`, `storage/` 독립 | CLAUDE.md에서 Repository와 DataSource는 별개 레이어로 정의 |
| `core/` 미사용 | 단일 모듈에서 "나머지 전부"를 묶는 것은 무의미한 중첩 |

### 삭제/정리 대상

| 대상 | 처리 |
|------|------|
| `webview/` (빈 폴더) | 삭제 |
| `test/` (main 소스) | 용도 확인 후 debug/ rename 또는 test 소스셋 이동 |
| `SNUTTUtils.kt` (패키지 루트) | lib/으로 이동 |

---

## 마이그레이션 순서

의존의 말단(다른 곳에서 많이 참조되지만, 자신은 이동 대상을 참조하지 않는 것)부터 이동한다.
각 단계는 "파일 이동 + import 경로 변경"을 한 커밋으로 완결한다.

### 1단계: 잡무 정리

변경 범위가 작고 다른 단계에 선행 조건이 없는 것들.

- [ ] `webview/` 빈 폴더 삭제
- [ ] `SNUTTUtils.kt` → `lib/`으로 이동

### 2단계: `domainmodel/` → `domain/model/`

- 현 `domain/`의 UseCase 등은 `domain/` 루트에 그대로 유지
- `domainmodel/`의 모든 파일을 `domain/model/`로 이동
- 참조처 (data, views, network 등) import 경로 일괄 변경

### 3단계: `ui/` → `ui/theme/`

- 현 `ui/Colors.kt`, `ui/Theme.kt`, `ui/Typo.kt` → `ui/theme/`로 이동
- 참조처 import 변경

### 4단계: `components/` → `ui/components/`

- `components/compose/`, `components/view/` 전체를 `ui/components/`로 이동
- 참조처 import 변경

### 5단계: `navigation/` + `deeplink/` 통합

- `deeplink/DeeplinkParser.kt` → `navigation/`으로 이동
- `views/`의 `NavigationDestination.kt`, `NavigationResult.kt` → `navigation/`으로 이동
- `views/RootNavGraph.kt` → `navigation/`으로 이동
- 참조처 import 변경

### 6단계: `views/` → `feature/` (평탄화)

가장 큰 변경. 하위 단계로 나눠서 진행.

- [ ] 6-1: `views/logged_out/` → `feature/login/` (+ `reset_password/` 포함 여부 결정)
- [ ] 6-2: `views/logged_in/` 직속 기능들 이동 (lecture_detail, notifications, table_lectures, thememarket, vacancy_noti)
- [ ] 6-3: `views/logged_in/home/` 직속 기능들 평탄화 (bookmark, friend, search, settings, review)
- [ ] 6-4: `views/logged_in/home/settings/` 하위 평탄화 (diary, theme → feature/ 직속으로)
- [ ] 6-5: `views/logged_in/home/` 잔여 (home 자체 + drawer + timetable + popups) → `feature/home/`
- [ ] 6-6: `views/` 루트 파일 이동 (RootActivity.kt, GlobalContext.kt → 패키지 루트)
- [ ] 6-7: `views/` 폴더 삭제

### 7단계: 후속 정리

- [ ] `test/` 처리 (용도 확인 후 결정)
- [ ] `lib/` 내부 정리 (개별 파일의 적절한 목적지 분석 — 별도 작업)
