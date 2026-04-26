# State Hoisting 백로그

이 문서는 preview 전수조사 (휴리스틱 §3) 중 발견된 **state hoisting 누락 후보**의
리스트업이다. 별도 PR 트랙으로 feature 별로 진행한다 — 이번 chore/preview-full 트랙에서
일괄 처리하기에는 ViewModel/UiState 변경 범위가 너무 큼.

## 진단 기준 (CLAUDE.md "원칙")

> 다음 두 가지 조건을 만족하는 값만 Screen 및 하위 컴포저블이 상태로 가질 수 있다.
> - 비즈니스 로직과 전혀 무관
> - 외부 라이프사이클에 따라 유지 혹은 복구될 필요 없음

위 조건을 만족하지 않는 `var x by remember { mutableStateOf(...) }` 패턴은 hoist 후보.
(다이얼로그/시트 내부 임시 상태처럼 정책상 로컬 유지가 정당한 케이스는 보류 — §3 참조)

---

## 1. 우선순위 높음 — 비즈니스 입력값

### 1.1 login feature
코드에 이미 `// TODO: ViewModel 로` 주석 존재.

| 파일 | 함수 | 변수 | 비고 |
|---|---|---|---|
| `feature/login/EmailVerificationPage.kt` | `EmailVerificationPage` | `flowState` | UI 분기 enum, ViewModel 로 |
| 〃 | `EmailVerificationScreen` | `codeField` | 인증코드 입력 |
| `feature/login/FindIdPage.kt` | `FindIdScreen` | `emailField` | 이메일 입력 (TODO 주석) |
| `feature/login/SignInPage.kt` | `SignInScreen` | `idField`, `passwordField` | 로그인 폼 (TODO 주석) |
| `feature/login/SignUpPage.kt` | `SignUpScreen` | `idField`, `passwordField`, `passwordConfirmField`, `emailField` | 회원가입 폼 (TODO 주석) |
| `feature/login/resetpassword/CheckIdStep.kt` | `CheckIdStep` | `idField` | `FindPasswordViewModel.UIState.CheckId` 로 |
| `feature/login/resetpassword/EnterFullEmailStep.kt` | `EnterFullEmailStep` | `emailField` | 〃 EnterFullEmail |
| `feature/login/resetpassword/NewPasswordStep.kt` | `NewPasswordStep` | `newPasswordField`, `newPasswordConfirmField`, `showErrorDialog`, `errorDialogTitle` | 〃 EnterNewPassword. dialog 가시성은 dialogState 로 |
| `feature/login/resetpassword/VerifyCodeStep.kt` | `VerifyCodeStep` | `codeField`, `showWhyNotCodeComingDialog` | 〃 VerifyCode. dialog 가시성은 dialogState 로 |

### 1.2 settings feature

| 파일 | 함수 | 변수 | 비고 |
|---|---|---|---|
| `feature/settings/AppReportPage.kt` | `AppReportScreen` | `email`, `detail`, `sentEnabled` | `AppReportViewModel` UiState 로 |
| `feature/settings/ChangeNicknamePage.kt` | `ChangeNicknameScreen` | `nicknameField` | `ChangeNicknameViewModel` UiState 로 |
| `feature/settings/UserConfigDialogs.kt` | `UserConfigDialogs` (ChangePassword 분기) | `currentPassword`, `newPassword`, `newPasswordConfirm` | `UserConfigViewModel` 로 |
| 〃 | (AddIdPassword 분기) | `id`, `password`, `passwordConfirm` | 〃 |

### 1.3 friend feature

| 파일 | 함수 | 변수 | 비고 |
|---|---|---|---|
| `feature/friend/FriendsBottomSheet.kt` | `RequestWithNicknameBottomSheet` | `nickname` | `FriendsViewModel` UiState 로 |
| 〃 | `EditDisplayNameBottomSheet` | `displayName` | 〃 |

### 1.4 diary feature

| 파일 | 함수 | 변수 | 비고 |
|---|---|---|---|
| `feature/diary/diarywrite/DiaryWritePage.kt` | `DiaryWriting` | `commentText` | `DiaryWriteViewModel` UiState 로. 이미 hoist 된 `isMoreTextExpanded` 옆 |

---

## 2. 우선순위 중간 — 다이얼로그 가시성 / 입력 버퍼

dialog 가시성은 정책상 `dialogState` 로 hoist 권장. 입력 버퍼는 케이스별 판단.

| 파일 | 함수 | 변수 | 판단 |
|---|---|---|---|
| `feature/themeconfig/ThemeDetailComponents.kt` | `ColorEditItem` | `showFgPicker`, `showBgPicker` | dialog 가시성 → `ThemeDetailViewModel.dialogState` |
| `feature/home/drawer/HomeDrawerDialogs.kt` | `HomeDrawerDialogs` (ChangeTableName 분기) | `newTitle` | 입력 버퍼. 코드에 "FIXME: 중복 코드" |
| `feature/home/timetable/TimeTableDialogs.kt` | `TimeTableDialogs` (ChangeTableName 분기) | `newTitle` | 위와 동일 (FIXME 중복) |
| `feature/home/drawer/bottomsheet/CreateNewTableSheetContent.kt` | `CreateTableBottomSheet` | `title`, `pickedCourseBook`, `clearFocusFlag` | 시트 내 입력 버퍼 |
| `feature/lecturedetail/currenttable/DayTimePickerSheetContent.kt` | `DayTimePickerSheetContent` | `dayIndex`, `startMinute`, `endMinute`, `pickerDialog` | 시트 내 임시 — 정책상 로컬 유지 정당 가능 |

---

## 3. 우선순위 낮음 — UI 애니메이션 / 임시 버퍼

| 파일 | 함수 | 변수 | 판단 |
|---|---|---|---|
| `feature/lecturedetail/LectureSessionSection.kt` | `LectureSessionListSection` | `lastItemAnimState` | 애니메이션 상태 — 로컬 유지 가능 |
| `feature/lecturedetail/LectureDetailInfoFields.kt` | `LectureDetailInfoFields` | `creditText` | Long → String 변환 버퍼. 코드에 "EditText 가 String 버퍼 자체 지원해야" FIXME |
| `feature/lecturedetail/ColorSelectorContent.kt` | `ColorPickerDialog` | `currentColor` | 다이얼로그 내부 임시 — 로컬 유지 정당 |
| `feature/lecturedetail/currenttable/DayTimePickerSheetContent.kt` | `DayPickerDialog` / `TimePickerDialog` | `tempIndex` / `tempMinute` | 〃 |
| `feature/themeconfig/ThemeDetailComponents.kt` | `ColorPickerDialog` | `currentColor` | 〃 |
| `feature/home/timetable/ScrollableTimetable.kt` | `ScrollableTimetableContent` | `scrollUnlocked` | 외부 prop 에서 파생 + 영구 토글. 의도 재검토 필요 |

---

## PR 트랙 권장

feature 단위로 PR 분리:

1. **login feature PR** — login + resetpassword (8개 hoist). TODO 주석 다수 해소 가치 큼.
2. **settings feature PR** — AppReport / ChangeNickname / UserConfig (5개 hoist).
3. **friend + diary 묶음 PR** — 작은 변경 (3개 hoist).
4. **dialog 가시성 일괄 PR** — `dialogState` 로 hoist (themeconfig, home 다이얼로그들).
5. **나머지 (우선순위 낮음)** — 컴포넌트 자체 개선과 함께 처리 (예: `EditText` 의 String 버퍼 개선).

각 PR 단위로 ViewModel/UiState 변경 + Route/Screen props 갱신 + 검증.
