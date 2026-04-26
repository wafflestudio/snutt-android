# Compose Preview 정책 (main 소스셋)

이 문서는 SNUTT Android 의 `main` 소스셋 `@Preview` 정책을 기술한다.
`screenshotTest` 의 `@Preview` 는 시각 회귀 방어막으로 정책이 명백히 다르므로
`docs/screenshot-test-policy.md` 를 별도 참조한다.

---

## 1. 목적

- **개발자의 시각적 단서**. 컴포넌트가 어떻게 생겼는지 인지하고, Preview 패널에서 컴포넌트 호출부를 클릭해
  소스 코드로 점프하는 IDE 기능을 적극 활용한다.
- 시각 회귀 방어는 `screenshotTest` 가 담당한다. 이 정책의 preview 들은 회귀 방어막이 아니다.

---

## 2. 작성 단위

### 2.1 컴포넌트 단위

- 해당 컴포넌트가 "어떻게 생겼는지" 개발자가 인지할 수 있을 정도의 **대표 분기**만 포함한다.
- 파라미터 곱집합도, 모든 `when`/`if` 분기 1:1 도 아니다 — 그건 `screenshotTest` 의 책임이다.
- 예: `LectureActionButtons` 는 버튼 3종(강의평/삭제/초기화) 의 생김새만 확인되면 충분 → 3장.

### 2.2 Screen 단위

- 화면 골격이 통째로 바뀌는 **큼직한 분기**만 여러 장 그린다.
    - 예시: 로그인 전/후, 빈 상태/로드 완료, 검색 결과 vs 검색 전 등.
- 그 외에는 가장 대표적이고 포괄적인 상태 **1개**만 그린다.
    - 다이얼로그·바텀시트 열림/닫힘, 편집 모드 토글, 로딩 spinner 노출 같은 작은 분기는 별도 preview 로 늘리지 않는다.
- "여러 장" 의 기준은 피처마다 다르며 case-by-case 로 결정한다. 공통 정책으로 묶지 않는다.
  통상 2장을 초과하는 일은 드물다.

### 2.3 작성 범위

- **모든 Screen·컴포넌트에 꼼꼼히 부착**하는 것을 목표로 한다 (시각적 단서 목적이므로 누락은 누구에게도 도움되지 않음).

---

## 3. 멀티 컨피그 기본값 — light/dark, locale

`@SnuttPreview` (`com.wafflestudio.snutt2.ui.preview.SnuttPreview`) 어노테이션을 모든 main preview 에
기본 적용한다. 이 어노테이션은 다음을 자동 부착한다:

- 라이트모드 + 다크모드 두 entry 의 **병렬 렌더링**.
- `locale = "ko"`.

`showBackground` 는 사용하지 않는다. 아래 §4 의 wrapper 가 `Surface` 를 함께 감싸기 때문에 theme 의
background color 가 자동으로 그려지며, `showBackground=true` 는 redundant 일 뿐만 아니라 다크모드에서
의도치 않은 흰 배경을 깔 수 있다.

> 표시 순서: Preview 패널은 name 의 알파벳 순으로 정렬하므로, 라이트모드가 먼저 나오도록 어노테이션 내부
> 에서 `"1. Light"`, `"2. Dark"` prefix 를 사용한다.

---

## 4. SNUTTTheme 적용 — wrapper composable

매 preview 함수는 `SnuttPreviewSurface { ... }` 로 컴포넌트를 감싼다.

```kotlin
@SnuttPreview
@Composable
private fun ExampleComponent_Default() {
    SnuttPreviewSurface {
        ExampleComponent(...)
    }
}
```

설계 메모:

- 어노테이션 단에서 `SNUTTTheme` 적용은 불가능하다 (Compose runtime 동작이 필요하므로).
- 공용 wrapper composable 을 써도 IDE 클릭-소스 점프 (preview 함수 안의 컴포넌트 호출부 → 정의로 점프)
  는 정상 동작한다 — POC 로 `FriendsBottomSheet.RequestMethodListBottomSheetPreview` 에서 검증함.
- `SnuttPreviewSurface` 는 `SNUTTTheme + Surface` 두 줄을 감추는 얇은 wrapper 다. 그 이상 비즈니스
  로직을 끼워 넣지 않는다. 이 wrapper 의 존재 의의는 boilerplate 제거 + theme/Surface 적용 누락 방지.

---

## 5. 사이즈 / 추가 옵션 오버라이드

기본적으로 `@SnuttPreview` 만 부착하면 된다 — `widthDp` / `heightDp` 는 지정하지 않는다 (wrap content).

### 5.1 사이즈 지정이 필요한 경우

**IDE 의 Preview 패널에서 시각적으로 잘림이 명확히 확인된 경우에만** 적용한다. 정적 분석/추정으로
일괄 적용하지 않는다 — 일괄 적용은 잘리지 않는 컴포넌트에 빈 공간을 만들고, `@SnuttPreview`
와 inline 두 entry 가 섞여 일관성을 해친다.

해당 preview 함수에 한해 `@SnuttPreview` 대신 **light/dark 두 `@Preview` 를 직접 부착**한다.

```kotlin
@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1100)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1100)
@Composable
private fun DiaryWriting_InProgress() { ... }
```

- `@SnuttPreview` 와 추가 `@Preview(heightDp=...)` 를 함께 쓰면 entry 가 (light + dark) +
  (default uiMode 의 사이즈 지정) 3개로 늘어나 의도와 어긋난다. 따라서 multipreview 를 안 쓰고
  inline 으로 두 entry 를 직접 작성한다.
- 사이즈 값은 케이스마다 다르므로 (Screen 의 컨텐츠 길이에 따라) 통일된 default 를 두지 않는다.

### 5.2 multipreview annotation 추출 (3-strikes rule)

같은 사이즈 케이스가 **3건 이상 누적**되면 그때 `@SnuttPreviewTall` 등의 multipreview annotation
추출을 검토한다. 그 전에는 inline 두 entry 로 유지 — 사이즈 케이스가 충분히 누적되지 않은 시점에
미리 추상화하는 것은 over-engineering.

---

## 6. Mock 데이터 중앙화

preview 가 사용하는 mock fixture 는 file-private 으로 두지 않고, 중앙 집결지에 모은다.

- 일반 fixture: `com.wafflestudio.snutt2.domain.model.preview.PreviewData`
- 도메인별 fixture: `DiaryPreviewData` 처럼 도메인 접두 + `PreviewData` 접미.

명명 컨벤션은 `sample*` (예: `sampleLectures`, `sampleDiarySummaryShortComment`).

이렇게 하면:
- 같은 fixture 를 여러 preview 에서 재사용 가능.
- mock 데이터 자체의 일관성 (실제 도메인 모델 구조 변경 시 한 곳만 수정).
- 컴포저블 파일이 짧아짐.

---

## 7. screenshotTest 와의 관계

- main 의 `@Preview` 는 **screenshotTest 의 입력이 아니다.** `com.android.compose.screenshot` 플러그인은
  `screenshotTest/` 소스셋의 `@Preview` 만 스캔한다.
- 두 소스셋의 정책은 독립이다. main preview 를 자유롭게 수정/추가/삭제하더라도 골든 이미지는 영향받지 않는다.
- 역할 비교:

  | 축       | `main` 의 `@Preview`                      | `screenshotTest` 의 `@Preview`     |
    |---------|------------------------------------------|----------------------------------|
  | 목적      | 개발자의 **시각적 단서**                          | 코드 변경에 따른 **의도치 않은 시각 회귀 방어**     |
  | 커버리지 기준 | 인지할 수 있을 정도의 대표 분기                       | 유의미한 모든 분기 1:1                    |
  | 변경 자유도  | 자유                                       | 의도된 UI 변경 시에만, 골든 갱신 필요           |
  | 파일 위치   | 컴포저블과 동일 파일                              | `app/src/screenshotTest/...` 대응 경로 |

---

## 8. 체크리스트

새 컴포넌트 / 화면을 작성할 때:

- [ ] 컴포넌트 preview 인가, 화면(Route/Screen) preview 인가? 단위에 맞는 분기 수를 가졌는가?
    - 컴포넌트: 대표 분기만 (모든 분기가 아님).
    - 화면: 화면 골격이 바뀌는 큼직한 분기만 + 그 외에는 대표 1개.
- [ ] `@SnuttPreview` 가 부착되어 있는가?
- [ ] `SnuttPreviewSurface { ... }` 로 컴포넌트를 감쌌는가?
