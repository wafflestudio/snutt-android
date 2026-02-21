# SNUTT 색깔 및 테마 정책

## 1. 테마 (Theme)

시간표(`TableDto`)는 테마를 가지며, 두 종류가 있다.

### BuiltInTheme (빌트인 테마)
- `TableDto.theme` 필드로 식별 (0-based code)
- 6가지: SNUTT(0), MODERN(1), AUTUMN(2), CHERRY(3), ICE(4), GRASS(5)
- 각 테마는 **9개의 색깔 팔레트**를 가짐
- **라이트/다크 모드별로 별도 팔레트** 존재 (코드에 하드코딩)
- 정의 위치: `TableTheme.kt`의 `BuiltInTheme.Companion`

### CustomTheme (커스텀 테마)
- `TableDto.themeId` 필드로 식별
- 테마 세부 정보는 별도 API로 가져옴 (`ThemeRepository.getTheme(themeId)`)
- **라이트/다크 모드 구분 없이 단일 팔레트**

### 테마 결정 우선순위 (`GetCurrentTableThemeUseCase`)
1. `themeId`가 있으면 -> CustomTheme
2. `themeId`가 없고 `theme`가 있으면 -> BuiltInTheme.fromCode(theme)
3. 둘 다 없으면 -> BuiltInTheme.SNUTT (기본값)

---

## 2. 강의 색깔 (Lecture Color)

`LectureDto`는 `colorIndex: Long`과 `color: ColorDto` 두 필드를 가진다.

### 색깔의 세 가지 유형

| 유형 | colorIndex | color 필드 | 조건 |
|------|-----------|-----------|------|
| 빌트인 테마의 팔레트 색깔 | 1~9 (1-based) | 비어있음 | 빌트인 테마 사용 시 |
| 빌트인 테마의 커스텀 색깔 | 0 | fg/bg 값 있음 | 빌트인 테마에서 사용자가 hex 지정 |
| 커스텀 테마의 색깔 | 0 | fg/bg 값 있음 | 커스텀 테마 사용 시 |

**중요**: `LectureDto`만으로는 "빌트인 테마의 커스텀 색깔"과 "커스텀 테마의 색깔"을 구분할 수 없다. 구분하려면 시간표의 테마 정보가 필요하다.

### 색깔 구성
- `foreground`: 글자색
- `background`: 배경색

---

## 3. 다크모드 처리

| 테마 유형 | 다크모드 처리 |
|----------|-------------|
| BuiltInTheme | `lightColors` / `darkColors` 별도 팔레트 (하드코딩) |
| CustomTheme | 단일 팔레트 (다크/라이트 구분 없음) |

다크모드 판단은 UI 레이어에서 `isDarkMode`로 수행한다.

---

## 4. 프리뷰 모드

테마 변경 시 미리보기 기능이 있다.

### CustomTheme 프리뷰
- 시간표 내 강의를 **인덱스 기반으로 커스텀 테마의 팔레트를 순환**하여 표시
- `colors[idx % colors.size]`
- colorIndex를 0으로, color를 해당 팔레트 색으로 설정

### BuiltInTheme 프리뷰
- 마찬가지로 **인덱스 기반으로 순환**하여 표시
- `colorIndex = idx % 9L + 1`
- 1-based index 사용

---

## 5. 현재 도메인 모델 (`LectureColor.kt`)

```kotlin
interface LectureColor {
    val foreground: Color  // androidx.compose.ui.graphics.Color
    val background: Color
    fun toColorDto(): ColorDto
}

data class CustomColor(foreground, background) : LectureColor
data class BuiltInColor(foreground, background, colorIndex: Long) : LectureColor
```

### 문제점
- `androidx.compose.ui.graphics.Color`에 의존 -> 도메인 모델이 UI 프레임워크에 결합
- `toColorDto()` 변환도 Compose의 `toArgb()` 사용

---

## 6. 설계 의사결정

### 결정 1: 색깔 값의 표현 — ARGB Int

순수 Kotlin 도메인 모델을 위해 `androidx.compose.ui.graphics.Color`를 대체할 타입이 필요하다.

**결론**: ARGB `Int`를 그대로 사용한다.

**대안 및 기각 사유**:
- Hex String: 서버 응답 형태와 동일하지만, UI에서 `Color(Int)`로 변환할 때 파싱이 필요
- ARGB Long: Compose Color 내부 표현과 유사하지만, 실제 Compose의 `Color()` 생성자와 `.toArgb()`가 Int 기반
- `@JvmInline value class SColor(val argb: Int)`: 타입 안전성은 있지만, 필드명(`foreground`, `background`)이 이미 의미를 나타내고 색깔 값이 항상 구조체 안에 있어 혼동 위험이 낮음. 실질적 이득 대비 보일러플레이트만 추가

**선택 사유**: Compose UI에서 `Color(argbInt)` 형태로 사용하므로 직접 매핑. 서버 hex string → Int 변환은 DTO → 도메인 변환 시 한 번만 수행.

### 결정 2: BuiltInColor는 인덱스만 보유

빌트인 테마의 강의 색깔을 도메인 모델에서 어떻게 표현할지 결정이 필요하다.

**결론**: 강의의 BuiltInColor는 `colorIndex`만 가진다. 실제 색깔 resolve는 UI 레이어에서 (테마 + 다크모드 조합으로) 수행한다.

**대안 및 기각 사유**:
- 색깔 값도 보유 (현재 방식): 다크모드 전환 시 재계산 필요, 테마 변경/프리뷰 시 동기화 부담
- 인덱스 + 라이트 색깔 + 다크 색깔 모두 보유: 데이터 중복. 색깔은 `(테마, 인덱스, 다크모드)`의 함수인데 그 결과를 중복 저장하는 셈

**선택 사유**: 색깔은 `(테마, 인덱스, 다크모드)`의 함수이다. 강의 입장에서는 "나는 몇 번 색이다"만 알면 충분하고, 실제 색 resolve는 렌더링 시점에 하는 게 자연스럽다. 테마 변경이나 프리뷰 모드에서도 resolve를 다시 하면 되므로 동기화 문제가 없다.

### 결정 3: 프리뷰 모드는 UI 레이어에서 처리

프리뷰 모드에서 강의 색깔을 어떻게 override할지 결정이 필요하다.

**결론**: 프리뷰는 도메인 모델을 변경하지 않고, UI 상위 컴포저블에서 색을 resolve한 뒤 하위 셀 컴포저블에는 resolve된 색깔 값(Int)만 전달한다.

**선택 사유**: 프리뷰 여부는 UI 상태이므로 UI 레이어의 책임이다. 현재 코드도 이미 이 패턴을 따르고 있다 — 상위에서 `bgColor: Int`, `fgColor: Int`를 계산하여 셀 컴포저블에 전달하고 있으므로 (`TimeTable.kt` L279-280), 셀 컴포저블은 테마에 대해 알 필요가 없다.

### 결정 4: 테마 팔레트 항목 — ThemeColor 별도 타입

테마 팔레트의 색깔 항목을 어떤 타입으로 표현할지 결정이 필요하다.

**결론**: `data class ThemeColor(val foreground: Int, val background: Int)` 별도 타입을 사용한다.

**대안 및 기각 사유**:
- `LectureColor.Custom` 재사용: 구조는 동일하지만, "팔레트 항목"과 "강의의 커스텀 색깔"은 의미가 다름

**선택 사유**: 팔레트 항목은 테마에 속한 색깔 옵션이고, `LectureColor.Custom`은 강의가 가진 커스텀 색깔이다. 의미가 다른 개념은 별도 타입으로 분리.

---

## 7. 새 도메인 모델 설계

### LectureColor (sealed interface)
강의가 자기 색깔에 대해 아는 것.
```kotlin
sealed interface LectureColor {
    data class BuiltIn(val colorIndex: Long) : LectureColor
    data class Custom(val foreground: Int, val background: Int) : LectureColor
}
```
- `BuiltIn`: 테마 팔레트의 인덱스 참조 (1-based). 실제 색은 UI에서 resolve.
- `Custom`: 사용자가 지정한 색깔 (ARGB Int). 빌트인 테마의 커스텀 색깔, 커스텀 테마의 색깔 모두 해당.

### ThemeColor (data class)
테마 팔레트의 항목. resolve의 결과물이기도 하다.
```kotlin
data class ThemeColor(val foreground: Int, val background: Int)
```
- `BuiltInTheme.lightColors`, `darkColors`: `List<ThemeColor>` (각 9개)
- `CustomTheme.colors`: `List<ThemeColor>`
- `LectureColor.BuiltIn`을 resolve하면 `ThemeColor`가 나온다.

### 관계
```
LectureColor.BuiltIn(colorIndex=3)
    --resolve(theme, isDarkMode)--> ThemeColor(fg, bg)

LectureColor.Custom(fg, bg)
    --직접 사용--> (fg, bg)
```

---

## 8. 관련 파일 위치
- 도메인 모델: `domainmodel/LectureColor.kt`, `domainmodel/TableTheme.kt`
- DTO: `lib/network/dto/core/LectureDto.kt`, `lib/network/dto/core/ColorDto.kt`
- UseCase: `domain/GetCurrentTableThemeUseCase.kt`
- UI: `views/logged_in/home/timetable/TimeTable.kt`
