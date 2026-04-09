# 강의 일기장 기능 명세

## 기능 개요

강의 일기장은 유저가 수강한 강의에 대한 일기를 작성하고 조회할 수 있는 기능이다.

- **일기 작성**: 푸시 알림을 통해 트리거되며, 활동 유형 선택 → 질문 응답 → 제출의 흐름으로 진행
- **일기 조회**: 더보기 > 강의 일기장 메뉴에서 학기별/날짜별로 작성한 일기 확인 및 삭제

---

## 유저 시나리오

### 시나리오 1: 강의 일기 작성

1. 유저가 푸시 알림을 수신한다.
    - 푸시 알림의 `intent.extra`에 `url_scheme` 키로 딥링크 URL이 포함됨
    - 형식: `snutt://diary?lectureId={lectureId}&courseTitle={courseTitle}`

2. 푸시 알림을 탭하면 앱이 열리고 일기 작성 화면으로 이동한다.
    - `RootActivity.parseDeeplinkExtra()`에서 `intent.extra`의 `url_scheme` 값을 `intent.data`로 설정
    - Jetpack Navigation이 딥링크를 인식하여 `NavigationDestination.LectureDiaryWrite`로 라우팅

3. **활동 선택 단계** (`ActivitySelectionState.InitialSelecting`)
    - 화면 상단에 강의명과 안내 문구 표시: "오늘 수강한 '{강의명}'에 대한 의견을 남겨보세요."
    - "오늘 무엇을 했나요?" 질문과 함께 활동 유형 버튼들 표시 (중복 선택 가능)
    - "완료" 버튼을 눌러 활동 선택 완료 → questionnaire API 호출

4. **질문 응답 단계** (`ActivitySelectionState.Complete`)
    - 선택한 활동에 따른 질문들이 표시됨
    - 각 질문에 대해 단일 선택으로 응답
    - (선택) "더 남기고 싶은 말을 작성해주세요." 섹션을 펼쳐 추가 코멘트 작성 (최대 200자)
    - 모든 질문에 응답하면 "다음" 버튼 활성화
    - **활동 재선택**: 이 단계에서 활동 버튼을 다시 탭하면 `ReSelecting` 상태로 전환되고 "완료" 버튼이 다시 표시됨. "완료"를 누르면
      questionnaire API를 재호출하여 새 질문을 받아옴

5. **완료 화면**
    - "강의일기가 등록되었습니다." 메시지 표시
    - 다음 강의가 있는 경우: "더 기록하기" 버튼 표시 → 다음 강의의 일기 작성으로 이동
    - 다음 강의가 없는 경우: "강의평 남기기" 버튼 표시 (현재 미구현)
    - "홈으로" 버튼으로 홈 화면 복귀

### 시나리오 2: 강의 일기 조회

1. 유저가 더보기(설정) 페이지에서 "강의 일기장" 메뉴를 탭한다.

2. 강의 일기장 화면이 표시된다.
    - 상단에 학기 선택 탭 (예: "25년 1학기", "24년 2학기")
    - 학기를 선택하면 해당 학기의 일기 목록 표시

3. 날짜별로 그룹화된 일기 목록이 표시된다.
    - 날짜와 강의명 요약이 표시됨
    - 날짜를 탭하면 해당 날짜의 일기들이 펼쳐짐

4. 펼쳐진 일기에서 상세 내용을 확인한다.
    - 강의명
    - 각 질문과 선택한 답변
    - 추가 코멘트 (있는 경우)

5. 휴지통 아이콘을 탭하여 일기를 삭제할 수 있다.

---

## API 명세

### 1. GET /v1/diary/dailyClassTypes

활동 유형 목록을 조회한다.

**요청**

- 파라미터 없음

**응답**

```json
[
  {
    "id": "activity_1",
    "name": "수업"
  },
  {
    "id": "activity_2",
    "name": "과제"
  },
  "..."
]
```

**요청 필드 수집 출처**: 없음

**응답 필드 사용 방식**
| 필드 | 사용 |
|------|------|
| `id` | 사용하지 않음 (도메인 모델에는 포함되어 있으나, API 요청 시 id가 아닌 name을 식별자로 사용) |
| `name` | 활동 선택 UI에 버튼 텍스트로 표시, 이후 API 요청(questionnaire, 일기 제출) 시 활동 식별자로 사용 |

---

### 2. POST /v1/diary/questionnaire

선택한 활동에 따른 질문 목록을 조회한다.

**요청**

```json
{
  "lectureId": "lecture_123",
  "dailyClassTypes": [
    "수업",
    "과제"
  ]
}
```

**응답**

```json
{
  "lectureTitle": "컴퓨터프로그래밍",
  "questions": [
    {
      "id": "question_1",
      "question": "수업 난이도는 어땠나요?",
      "answers": [
        "쉬워요",
        "적당해요",
        "어려워요"
      ]
    },
    "..."
  ],
  "nextLectureId": "lecture_456",
  "nextLectureTitle": "자료구조"
}
```

**요청 필드 수집 출처**
| 필드 | 수집 출처 |
|------|----------|
| `lectureId` | 푸시 알림 딥링크의 query parameter (`NavigationDestination.LectureDiaryWrite.lectureId`) |
| `dailyClassTypes` | 유저가 활동 선택 UI에서 선택한 활동들의 `name` 값 리스트 |

**응답 필드 사용 방식**
| 필드 | 사용 |
|------|------|
| `lectureTitle` | 사용하지 않음 (딥링크에서 이미 `courseTitle`을 받아오므로 불필요) |
| `questions[].id` | 일기 제출 시 `questionId`로 사용 |
| `questions[].question` | 질문 텍스트로 UI에 표시 |
| `questions[].answers` | 답변 선택지 버튼으로 UI에 표시 |
| `nextLectureId` | 다음 강의 일기 작성 시 사용, null이면 "강의평 남기기" 버튼 표시 |
| `nextLectureTitle` | 다음 강의 일기 작성 화면의 강의명으로 사용 |

---

### 3. POST /v1/diary

일기를 제출한다.

**요청**

```json
{
  "lectureId": "lecture_123",
  "dailyClassTypes": [
    "수업",
    "과제"
  ],
  "questionAnswers": [
    {
      "questionId": "question_1",
      "answerIndex": 1
    },
    {
      "questionId": "question_2",
      "answerIndex": 2
    }
  ],
  "comment": "오늘 수업 재밌었어요"
}
```

**응답**

```json
{
  "message": "ok"
}
```

**요청 필드 수집 출처**
| 필드 | 수집 출처 |
|------|----------|
| `lectureId` | 푸시 알림 딥링크의 query parameter |
| `dailyClassTypes` | 유저가 선택한 활동들의 `name` 값 리스트 |
| `questionAnswers[].questionId` | questionnaire API 응답의 `questions[].id` |
| `questionAnswers[].answerIndex` | 유저가 선택한 답변의 index (0-based) |
| `comment` | 유저가 "더 남기고 싶은 말" 섹션에 입력한 텍스트 (최대 200자) |

---

### 4. GET /v1/diary/my

내가 작성한 일기 목록을 조회한다.

**요청**

- 파라미터 없음

**응답**

```json
[
  {
    "year": 2025,
    "semester": 1,
    "submissions": [
      {
        "id": "diary_123",
        "lectureId": "lecture_123",
        "date": "2025-03-20T14:30:00",
        "lectureTitle": "컴퓨터프로그래밍",
        "shortQuestionReplies": [
          {
            "question": "수업 난이도",
            "answer": "적당해요"
          },
          "..."
        ],
        "comment": "오늘 수업 재밌었어요"
      },
      "..."
    ]
  },
  "..."
]
```

**응답 필드 사용 방식**
| 필드 | 사용 |
|------|------|
| `year`, `semester` | 학기 탭 표시 (예: "25년 1학기") |
| `submissions[].id` | 일기 삭제 시 식별자로 사용 |
| `submissions[].lectureId` | 현재 직접 사용하지 않음 (도메인 모델에는 포함) |
| `submissions[].date` | 날짜별 그룹화 키, 날짜 포맷팅하여 표시 |
| `submissions[].lectureTitle` | 일기 상세에 강의명으로 표시 |
| `submissions[].shortQuestionReplies` | 일기 상세에 Q&A 형태로 표시 |
| `submissions[].comment` | 일기 상세에 "남기고 싶은 말"로 표시 (빈 문자열이면 null 처리) |

---

### 5. DELETE /v1/diary/{id}

일기를 삭제한다.

**요청**

- Path parameter: `id` - 삭제할 일기의 ID

**응답**

```json
{
  "message": "ok"
}
```

**요청 필드 수집 출처**
| 필드 | 수집 출처 |
|------|----------|
| `id` | GET /v1/diary/my 응답의 `submissions[].id` |

---

## 네비게이션 구조

### 진입점

1. **푸시 알림 → 일기 작성**
    - DeepLink: `snutt://diary?lectureId={lectureId}&courseTitle={courseTitle}`
    - Destination: `NavigationDestination.LectureDiaryWrite`

2. **더보기 > 강의 일기장 → 일기 조회**
    - 메뉴 위치: SettingsPage > "강의 일기장" (FeatureFlag.LECTURE_DIARY 활성화 시)
    - Destination: `NavigationDestination.LectureDiaryHistory`
    - DeepLink: `snutt://lecture_diary_history`

### 화면 간 이동

```
LectureDiaryWrite
  ├─ 완료 → 홈으로 버튼 → Home (navigateAsOrigin)
  ├─ 완료 → 더 기록하기 → LectureDiaryWrite (화면 내부에서 상태 리셋)
  ├─ X 버튼 → popBackStack
  └─ ForceLogout (인증 에러) → Onboard (navigateAsOrigin)

LectureDiaryHistory
  ├─ 뒤로가기 → popBackStack
  └─ (인증 에러 처리 없음, Toast만 표시)
```

---

## 활동 선택 상태 머신

일기 작성 시 활동 선택 단계는 `ActivitySelectionState`로 관리된다.

```
InitialSelecting ──(완료 클릭)──→ Complete
                                    │
                              (활동 버튼 탭)
                                    ↓
                               ReSelecting ──(완료 클릭)──→ Complete
```

| 상태                 | 설명                | "완료" 버튼 | 질문 섹션         |
|--------------------|-------------------|---------|---------------|
| `InitialSelecting` | 최초 활동 선택 중        | 표시      | 미표시           |
| `Complete`         | 활동 선택 완료, 질문 응답 중 | 미표시     | 표시            |
| `ReSelecting`      | 활동 재선택 중          | 표시      | 표시 (이전 질문 유지) |

**관련 코드**:

- 상태 정의: `DiaryWriteUiState.kt:34-39`
- 활동 토글 시 상태 전환: `DiaryWriteViewModel.kt:71-84`
- 완료 클릭 시 API 호출: `DiaryWriteViewModel.kt:160-190`
- UI에서 재선택 트리거: `DiaryWriteComponents.kt:77-82`

---

## 관련 코드 위치

### View Layer

- `views/logged_in/home/settings/diary/diary_write/`
    - `DiaryWritePage.kt` - Route, Screen 정의
    - `DiaryWriteViewModel.kt` - 비즈니스 로직
    - `DiaryWriteUiState.kt` - UI 상태 모델
    - `DiaryWriteComponents.kt` - 재사용 UI 컴포넌트
- `views/logged_in/home/settings/diary/diary_history/`
    - `DiaryHistoryPage.kt` - Route, Screen 정의
    - `DiaryHistoryViewModel.kt` - 비즈니스 로직
    - `DiaryHistoryUiState.kt` - UI 상태 모델
    - `DiarySummary.kt` - 일기 요약 컴포넌트

### Data Layer

- `data/lecture_diary/DiaryRepository.kt` - Repository 인터페이스
- `data/lecture_diary/DiaryRepositoryImpl.kt` - Repository 구현체

### Network Layer

- `lib/network/SNUTTRestApi.kt` - API 엔드포인트 정의
- `lib/network/dto/` - 요청/응답 DTO

### Domain Model

- `domainmodel/diary/DiaryModels.kt` - 도메인 모델

### Navigation

- `views/NavigationDestination.kt` - `LectureDiaryWrite`, `LectureDiaryHistory`
- `views/RootActivity.kt` - Composable graph 등록 (L444-471), 딥링크 파싱

---

## 참고 사항

- 현재 DEBUG 빌드에서만 강의 일기장 네비게이션 그래프가 등록됨 (`BuildConfig.DEBUG` 조건)
- FeatureFlag.LECTURE_DIARY 플래그로 설정 페이지 메뉴 노출 여부 제어
- "강의평 남기기" 기능은 현재 미구현 상태 (TODO 주석 존재)

## 알려진 임시 코드 / TODO

- `DiarySummary.kt:68` - 날짜별 강의명 요약 텍스트가 하드코딩되어 있음 ("시각디자인기초, 배구")
- `DiaryWritePage.kt:101-104` - onNavigateReview 콜백이 빈 구현
