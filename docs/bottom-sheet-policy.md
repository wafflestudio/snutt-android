# ModalBottomSheet 상태 관리 정책

## 배경

ModalBottomSheetLayout을 사용하면 두 개의 상태가 공존한다.

| 상태 | 소유자 | 의미 |
|---|---|---|
| `uiState.sheetType` | ViewModel | "어떤 시트를 보여줘야 하는가" (의도) |
| `sheetState` (`ModalBottomSheetState`) | Compose | 시트의 애니메이션·가시성 등 표시 상태 |

이 둘은 본질적으로 동기화가 불가능하다. `sheetState.hide()` 호출 후 애니메이션이 진행되는 동안 두 상태가 괴리되며, 이를 무시하면 다음과 같은 문제가 발생한다.

## 문제

### 1. 닫기 시점에 UiState를 즉시 갱신하면 UI 깨짐

바텀시트를 닫을 때 `sheetType`을 None으로 즉시 바꾸면, 닫기 애니메이션이 끝나기 전에 `sheetContent`가 비워진다. 시트가 슬라이드 다운되는 도중 내용이 사라지는 시각적 결함이 발생한다.

### 2. UiState를 갱신하지 않으면 상태 괴리

반대로 `sheetType`을 갱신하지 않으면, 시트가 이미 닫힌 뒤에도 ViewModel은 "시트가 열려 있다"고 인식한다. BackHandler 등 로직에서 잘못된 분기가 발생할 수 있다.

### 3. 시트가 닫히는 경로가 다양함

바텀시트는 다음 경로로 닫힐 수 있다:
1. ViewModel 비즈니스 로직에 의해 (UiEvent → Route → `sheetState.hide()`)
2. UI에서 hoist된 이벤트에 의해 (닫기 버튼 등 → ViewModel → UiEvent → Route)
3. ModalBottomSheetLayout 자체적으로 (scrim 클릭 등, `sheetGesturesEnabled = true`인 경우)

모든 경로에서 일관된 상태 정리가 필요하다.

## 결정

### `BottomSheetDismissEffect` 패턴 도입

`snapshotFlow`로 `sheetState.currentValue`를 관찰하여, 값이 `Hidden`으로 확정된 뒤에 ViewModel의 정리 함수를 호출한다. 이 로직을 `BottomSheetDismissEffect` 유틸로 캡슐화한다.

```kotlin
// Route에서 사용
BottomSheetDismissEffect(sheetState, vm::onSheetDismissed)

// ViewModel에서 정리
fun onSheetDismissed() {
    _uiState.update { it.copy(sheetType = SheetType.None) }
}
```

이 패턴을 통해:
- 시트 닫기 애니메이션이 완료된 뒤에만 UiState가 정리되므로 UI 깨짐이 없다.
- 닫히는 경로와 무관하게 `currentValue == Hidden` 시점에 항상 실행되므로 누락이 없다.
- ViewModel은 `closeSheet()`(명령)과 `onSheetDismissed()`(사후 정리)로 역할이 분리된다.

### `sheetState` 직접 접근 금지

`sheetState`의 속성(`isVisible`, `currentValue` 등)을 Route 로직에서 직접 읽지 않는다. 이를 허용하면 ViewModel의 UiState와 `sheetState` 중 어느 것이 진실의 원천(source of truth)인지 모호해지고, 시트 열림/닫힘 판단이 두 곳에 분산된다.

- BackHandler 조건: `uiState.sheetType != None` 사용 (`sheetState.isVisible` 아님)
- `sheetState`는 `ModalBottomSheetLayout`에 전달하고, UiEvent 핸들러에서 `show()`/`hide()`를 호출하기 위한 배관으로만 취급한다.
- `sheetState`의 관찰은 `BottomSheetDismissEffect` 내부에 캡슐화한다.

`sheetState.isVisible` 대신 UiState를 사용하는 이유: ViewModel이 "시트를 열겠다"는 의도를 설정한 뒤 `sheetState.show()`가 실제로 호출되기까지 수 프레임의 지연이 있다. 이 구간에서 `sheetState.isVisible`은 아직 false이지만, 사용자가 백키를 누르면 의도를 취소하는 것이 자연스럽다. UiState의 SheetType은 의도를 즉시 반영하므로 이 경우를 올바르게 처리한다.

### BackHandler는 Route에서만 사용

시스템 백 키 처리는 Route에서 통합 관리한다. 예외적으로, 하위 컴포저블이 자체 로컬 상태 기반의 서브 네비게이션을 가지는 경우(예: `TimeSelectSheet`의 시간 선택 ↔ 일반 모드 전환)에는 해당 컴포저블에서 BackHandler를 사용할 수 있다.

## 관련 파일

- `BottomSheetDismissEffect.kt`: snapshotFlow 기반 dismiss 감지 유틸
- `CurrentTableLectureDetailRoute.kt`: 대표적인 적용 예시
- `TimeTableRoute.kt`: 크로스-VM dismiss 처리 예시 (FIXME 참조)
