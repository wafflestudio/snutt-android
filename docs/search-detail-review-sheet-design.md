# 검색 페이지 강의상세 내 강의평 시트 설계 논의

## 상황

`SearchBottomSheetLayout`은 검색 결과에서 강의를 클릭했을 때 열리는 외부 바텀시트(outer sheet)를 관리한다.
`BottomSheetType.LectureDetail` 케이스에서 `LectureDetail` 컴포저블을 표시하는데,
`LectureDetail` 안에는 "강의평" 버튼이 있고, 클릭 시 **중첩 바텀시트(inner sheet)** 가 올라와야 한다.

이 inner sheet의 상태를 어디서 소유하고 제어할지가 논의 대상이다.

---

## 방법 A — ViewModel UiEvent 경유 (현재 구현)

```
LectureDetail 클릭 강의평
  → vm.openDetailReview()
    → UiState: bottomSheetType.reviewVisible = true
    → UiEvent: OpenDetailReviewSheet
  → Route(LaunchedEffect)
    → detailReviewWebViewContainer.openPage(url)
    → detailReviewSheetState.show()

CloseBridge / onBackPressed
  → vm.closeDetailReview()
    → UiState: bottomSheetType.reviewVisible = false
    → UiEvent: CloseDetailReviewSheet
  → Route(LaunchedEffect)
    → detailReviewSheetState.hide()
```

- `detailReviewSheetState`, `detailReviewWebViewContainer` — Route에서 생성, SearchBottomSheetLayout →
  SearchLectureDetailSheetContent로 파라미터 체인
- `reviewVisible` — `onBackPressed` 시 inner sheet가 열려 있는지 판단하는 용도로 UiState에 포함
- ViewModel에 `openDetailReview()`, `closeDetailReview()`, UiEvent 2종, `reviewVisible` 필드 추가 필요

**장점**

- CLAUDE.md 원칙 준수: "sheet 상태는 Route가 소유, ViewModel은 UiEvent로 제어 요청"
- ViewModel이 inner sheet 가시성을 추적 → 테스트 가능

**단점**

- `reviewVisible`, `openDetailReview()`, `closeDetailReview()`, 이벤트 2종이 순수 UI-only 로직인데 ViewModel에
  위치
- `detailReviewSheetState`, `detailReviewWebViewContainer` 파라미터가 Route → Layout → SheetContent 3단
  체인으로 전달됨

---

## 방법 B — SearchLectureDetailSheetContent 내부 폐쇄 처리

```
SearchLectureDetailSheetContent 내에서:
  val detailReviewSheetState = rememberModalBottomSheetState(...)
  val detailReviewWebViewContainer = remember { ReviewWebViewContainer(...) }
    CloseBridge: onClose → detailReviewSheetState.hide()

LectureDetail 클릭 강의평
  → detailReviewWebViewContainer.openPage(url)
  → scope.launch { detailReviewSheetState.show() }

onBackPressed
  → if (detailReviewSheetState.currentValue != Hidden) detailReviewSheetState.hide()
  → else onDismiss()
```

- `accessToken: StateFlow<String>` 파라미터 하나가 추가로 필요 (ReviewWebViewContainer 생성용)
- Route, SearchBottomSheetLayout은 inner sheet의 존재를 전혀 모름

**장점**

- inner sheet는 LectureDetail의 내부 UI 관심사이므로, 외부로 노출할 이유가 없음
- ViewModel에서 UI-only 로직 제거 (`reviewVisible`, `openDetailReview`, `closeDetailReview`, 이벤트 2종)
- 파라미터 체인 단순화 (detailReviewSheetState, detailReviewWebViewContainer, onCloseDetailReview 제거)

**단점**

- CLAUDE.md의 "sheet 상태는 Route가 소유" 원칙에서 벗어남
    - 단, 그 원칙은 Route-level sheet에 대한 것. 중첩 sheet는 명시적으로 다루지 않음
- `accessToken` 파라미터 하나 추가

---

## 핵심 트레이드오프

|                 | 방법 A                    | 방법 B                      |
|-----------------|-------------------------|---------------------------|
| 아키텍처 원칙 부합도     | CLAUDE.md 원칙 그대로        | 원칙 취지는 부합, 문자 그대로는 일부 벗어남 |
| ViewModel 오염    | UI-only 로직 포함           | 없음                        |
| 파라미터 체인         | 3단 (Route→Layout→Sheet) | 없음                        |
| 추가 파라미터         | 없음                      | `accessToken` 1개          |
| inner sheet 캡슐화 | 낮음 (Route까지 노출)         | 높음 (SheetContent 내 완결)    |

`reviewVisible`이 CLAUDE.md의 "composable이 가질 수 있는 상태" 조건("비즈니스 로직과 무관", "외부 라이프사이클 복구 불필요")에 부합하므로,
방법 B가 더 적합하다는 의견이 있다.

## 현재 상태

방법 A로 구현된 상태. 결론 미정.
