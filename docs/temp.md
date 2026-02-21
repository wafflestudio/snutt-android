Step 1: ThemeColor 타입 추가

- domainmodel/ThemeColor.kt 생성: data class ThemeColor(val foreground: Int, val background: Int)
- 기존 코드 변경 없음

Step 2: LectureColor를 sealed interface로 전환

- LectureColor를 interface → sealed interface로 변경
- CustomColor, BuiltInColor를 LectureColor.Custom, LectureColor.BuiltIn으로 내부 클래스화
- 하위 호환을 위해 typealias CustomColor = LectureColor.Custom / BuiltInColor = LectureColor.BuiltIn 추가
- 이 시점에서는 아직 Color (Compose) 유지

Step 3: ColorDto에 순수 Kotlin 유틸 추가

- parseHexColor(hex: String): Int — android.graphics.Color.parseColor 대체용 순수 Kotlin 함수
- ColorDto.toThemeColor(): ThemeColor — 확장함수
- 기존 코드 변경 없이 추가만

Step 4: TableTheme 팔레트를 ThemeColor로 전환 (가장 큰 단계)

- TableTheme의 List<LectureColor> → List<ThemeColor>
- BuiltInTheme 팔레트: BuiltInColor(fg, bg, idx) → ThemeColor(fg, bg)
- CustomTheme.colors: List<LectureColor> → List<ThemeColor>
- EditingTheme.colors: List<Selectable<LectureColor>> → List<Selectable<ThemeColor>>
- getColorByIndex(), getColorByIndexComposable() 제거 → 호출부에서 getColors(isDarkMode)[index] 직접 접근
- 영향받은 파일: ThemeDto, ThemeRepository, ThemeRepositoryImpl, ThemeDetailViewModel, ThemeDetailScreen,
  ThemeIcon, ColorBox, TimeTable, TimetableView,
  LectureColorSelectorPage, LectureDetailPage

Step 5: LectureColor에서 Color → Int 전환

- LectureColor.Custom/LectureColor.BuiltIn의 foreground: Color, background: Color → Int
- toColorDto()에서 .toArgb() 제거 (이미 Int)
- toCustomColor()에서 Color(hex.toColorInt()) → parseHexColor(hex)
- 영향받은 파일: LectureDto, TimetableLectureDto, PreviewData, ColorBox, LectureColorSelectorPage,
  LectureDetailPage

Step 6: BuiltIn에서 foreground/background 제거

- LectureColor.BuiltIn → colorIndex만 보유
- LectureColor sealed interface에서 공통 foreground/background 프로퍼티 제거
- toColorDto()를 인터페이스 메서드에서 제거 → Custom에서만 제공
- fromLocalLecture()에서 BuiltIn일 때: ColorDto() (빈 DTO) / null 반환
- 영향받은 파일: LectureDto, TimetableLectureDto, ColorBox

Step 7: typealias 및 레거시 제거

- typealias CustomColor/BuiltInColor 제거
- 모든 참조를 LectureColor.Custom/LectureColor.BuiltIn으로 교체
- toCustomColor() 확장함수 제거 → 호출부에서 toThemeColor() 사용
- 영향받은 파일: LectureDto, TimetableLectureDto, PreviewData, LectureDetailPage, LectureColorSelectorPage

Step 8: ColorDto/ColorSetDto에서 android.graphics.Color 제거

- Color.parseColor() → parseHexColor() (Step 3에서 추가한 순수 Kotlin 함수) 사용
- ColorDto.kt, ColorSetDto.kt 모두 적용

  ---
