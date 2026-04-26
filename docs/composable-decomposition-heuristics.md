# Composable 컴포넌트 분리 휴리스틱

이 문서는 SNUTT Android 의 Compose 컴포넌트를 어떤 단위로 추출/분리할지 판단할 때
참고하는 휴리스틱을 정리한다. 사례 기반이며, 새로운 사례가 발견될 때마다 누적한다.

`docs/preview-policy.md` (preview 작성 규칙) 와는 의도가 다르다 — preview 부착 작업
중 자연스럽게 노출된 **컴포넌트 분리/추출의 시그널**을 다룬다.

---

## 1. 핵심 진단

> preview 를 부착했을 때 시각이 어색하다면, 컴포넌트 분리 자체에 시그널이 있을 수 있다.

preview 는 시각 회귀 방어막이 아니라 **시각적 단서**(`preview-policy.md` §1)
이지만, 단독으로 그렸을 때 어색하다는 사실은 그 컴포넌트가 "단독 호출 가능한 의미
단위로 잘 추출됐는가" 를 검증하는 부수 효과를 갖는다.

이 검증을 무시하지 말고, 시그널의 종류를 식별한 뒤 적절히 대응한다.

---

## 2. 진단 분기

```
preview 부착 시 어색함 또는 redundancy 가 발견됨
  │
  ├─ 다른 컴포넌트의 preview 와 시각이 정확히 동일
  │   │
  │   ├─ 두 컴포넌트가 다른 책임 단위인데 시각이 우연히 동일
  │   │   → 책임 중복 시그널
  │   │     해소: 한 컴포넌트의 분기 책임을 호출자로 이전 (책임 분리)
  │   │     사례: §3.1
  │   │
  │   └─ 자식 컴포넌트의 시각이 부모 preview 에서 이미 완전히 보임
  │       → preview redundant 시그널 (분리 자체는 OK)
  │         해소: 자식의 별도 preview 만 제거 (분리는 유지)
  │         사례: §3.4
  │
  ├─ 같은 컴포넌트의 두 분기 preview 의 시각 차이가 작고 한쪽이 다른쪽의 superset
  │   → 분기 redundant 시그널
  │     해소: superset 분기 preview 만 유지, subset 제거
  │     사례: §3.5
  │
  └─ 단독 호출 시 시각이 빈 화면 또는 의도와 다름 (예: 라이트 모드 흰 화면)
      │
      ├─ 자체 시각 책임 (배경 등) 이 빠진 게 원인 — 모르고 빠뜨림
      │   → self-contained 부재 시그널
      │     해소: 자체 배경/시각 책임을 컴포넌트에 박는다
      │     사례: §3.2
      │
      └─ 호출 컨텍스트에 의존하는 게 의도된 디자인
          → 컨텍스트 의존 컴포넌트 시그널
            해소: preview 가 호출 컨텍스트를 inline 으로 흉내 (wrapper Box)
            사례: §3.3
```

자체 시각 책임이 "빠뜨려진 것" 인지 "의도된 의존" 인지의 구분은 디자인 의도를 보고
판단한다. 의심스러우면 디자이너/리뷰어와 상의한다.

---

## 3. 사례

### 3.1 책임 중복 — `BookmarkList_Empty` ≡ `BookmarkPlaceHolder_Empty`

**시그널**: 두 preview 의 시각이 정확히 동일.

**원인**:
- `BookmarkList` 가 "리스트 렌더링" + "비어있을 때 `BookmarkPlaceHolder` 호출"
  두 책임을 가지고 있었음.
- `BookmarkList(bookmarks = emptyList())` 는 `BookmarkPlaceHolder()` 와
  시각적으로 동일.

**해소**:
- 빈 상태 분기를 호출자 (`BookmarkPage`) 로 이전. `BookmarkList` 는 단일 책임
  (리스트 렌더링) 만 갖도록.
- `BookmarkList_Empty` preview 제거.
- 함께 옮긴 책임: `logImpression(AnalyticsScreen.Bookmark)` 도 호출자
  modifier 로 이전 (빈 상태에서도 동일하게 찍히도록).

**커밋**: `5c57ec29 refactor: BookmarkList 의 빈 상태 분기 책임을 호출자로 이전`

---

### 3.2 self-contained 부재 — `BookmarkPlaceHolder` 라이트 모드 흰 화면

**시그널**: 단독 preview 가 라이트 모드에서 빈 화면처럼 보임.

**원인**:
- `BookmarkPlaceHolder` 의 텍스트 색이 `SNUTTColors.White700` (light/dark 모두
  `0xb3ffffff`, 70% 알파 흰색).
- 라이트 surface (`White`) 위에 70% 알파 흰색 텍스트 → 거의 안 보임.
- 컴포넌트가 어두운 배경(예: `Dim2`) 위에서만 정상 그려지는데, 그 배경을
  호출자 (`BookmarkPage`) 가 modifier 로 깔아 주고 있었음.
- 외부 배경에 묵시적 의존 = self-contained 가 아님.

**해소**:
- `BookmarkPlaceHolder` 와 `BookmarkList` 모두 자체 `background(Dim2)` 를
  갖도록 변경.
- 호출자 (`BookmarkPage`) 의 `Modifier.background(Dim2)` 제거.
- `logImpression` 만 호출자 modifier 로 남김.

**커밋**: `9f7c9855 refactor: BookmarkList/BookmarkPlaceHolder 를 self-contained 컴포넌트로`

---

### 3.3 의도된 컨텍스트 의존 — `SearchLectureListItem_Collapsed` 라이트 모드 흰 화면

**시그널**: 단독 preview 가 라이트 모드에서 빈 화면처럼 보임 (3.2 와 동일 증상).

**원인**:
- `SearchLectureListItem` 의 코드:
  ```kotlin
  .background(if (lectureState.selected) SNUTTColors.Dim2 else SNUTTColors.Transparent)
  ```
- selected 분기에서만 `Dim2`, 그 외에는 `Transparent`.
- 텍스트 색은 `SNUTTColors.AllWhite` (항상 흰색).
- selected 시의 `Dim2` 는 단독 배경이 아니라 **부모의 dim 위에 누적되는 selection
  highlight**. 부모(`BookmarkList`, `SearchResultList`) 의 dim 이 깔린 상태에서
  추가 alpha 로 더 진해지는 효과가 의도.
- 즉 컴포넌트가 어두운 list 컨테이너 안에서만 의미 있게 동작 — 단독 시각 정체성을
  갖지 않는 게 의도.

**해소** (3.2 와 다른 방향):
- 디자인을 self-contained 로 바꾸는 것은 selection highlight 의미가 사라지거나
  새 색을 도입해야 하는 디자인 변경. 비용 큼.
- 대신 **preview 가 호출 컨텍스트를 inline 으로 흉내**:
  ```kotlin
  @SnuttPreview
  @Composable
  private fun SearchLectureListItem_Collapsed() {
      SnuttPreviewSurface {
          Box(modifier = Modifier.background(SNUTTColors.Dim2)) {
              SearchLectureListItem(...)
          }
      }
  }
  ```
- preview 가 컴포넌트의 진짜 사용 컨텍스트 (어두운 dim list 안) 를 명시적으로
  표현 — 자기설명적 documentation 역할도 함.

**왜 wrapper 인프라화 (`SnuttPreviewSurface(onDimContext = true)` 같은 옵션 추가)
는 보류했는가**:
- 비슷한 패턴이 여러 사례 (3-4 개 이상) 에서 반복되기 전에는 미리 추상화하지 않는다.
- inline wrapper 는 의도가 코드에 직접 보여 명시적이며, 추상화 시 컨텍스트 종류가
  늘어날 때 인프라가 ad-hoc 으로 부풀 위험을 회피.

**커밋**: `7925ce68 refactor: SearchLectureListItem preview 에 호출 컨텍스트 wrapper 추가`

---

### 3.4 자식 컴포넌트의 redundant preview — `DiarySummary_Default`

**시그널**: 부모(`DiarySummariesOfDay_Expanded`) preview 에서 자식(`DiarySummary`)
컴포넌트의 시각이 이미 완전히 노출되며, IDE 클릭으로 자식 정의로의 네비게이션도 부모를
통해 가능. 자식의 별도 preview 는 시각·네비게이션 가치가 모두 부모에 흡수되어 redundant.

**원인**:
- `DiarySummary` 는 `DiarySummariesOfDay` 안에서만 사용되는 private helper.
- 추출 자체는 가독성/책임 측면에서 합리 — **분리가 잘못된 것은 아님**.
- 다만 추출된 모든 함수마다 preview 를 부착해야 하는 것은 아님. 부모 preview 에서
  자식이 충분히 노출되면 별도 preview 의 가치가 사라짐.

**해소**:
- 자식의 별도 preview 만 제거.
- 컴포넌트 분리는 그대로 유지.
- 추가로 file-private fixture (`previewSummary1/2`) 는 중앙 집결지
  (`DiaryPreviewData`) 로 이전 (preview-policy.md §6 참조).

**판단 기준** (자식 preview 를 제거할지):
- 부모 preview 에서 자식이 의미 있게 노출되는가?
- 자식이 호출 컨텍스트 없이 단독으로 의미 있는 분기를 가지는가? (있다면 부모에서 못
  보여주는 분기가 있을 수 있음 → 자식 preview 유지)
- 부모 preview 에서 자식 호출부 클릭으로 자식 정의에 도달 가능한가?

**커밋**: `77461db7 refactor: DiarySummary 의 file-private fixture 를 DiaryPreviewData 로 이전 + redundant preview 제거`

---

### 3.5 같은 컴포넌트 분기 간 superset/subset — `DiaryActivitySelectSection_Completed`

**시그널**: 같은 컴포넌트의 두 분기 preview 의 시각 차이가 "한 element 의 가시성"
정도로 작고, 한쪽이 다른쪽의 시각 superset.

**원인** (`DiaryActivitySelectSection`):
- `Selecting` 분기: 선택 옵션 + "완료" 버튼.
- `Completed` 분기: 선택 옵션만 (완료 버튼 없음).
- 시각적으로 `Selecting` 이 `Completed` 의 superset. `Completed` 는 `Selecting`
  에서 완료 버튼만 빠진 모습이라 superset preview 만 보면 imagine 가능.

**해소**:
- superset 분기 preview (`Selecting`) 만 유지.
- subset 분기 preview (`Completed`) 제거.
- 컴포넌트의 분기 로직 자체는 그대로 (시각 코드는 두 분기 모두 정상 동작).

**판단 기준**:
- **시각 superset** (객관적): 한 분기의 시각 요소가 다른 분기의 시각 요소를 모두
  포함하고 추가 요소가 있는가?
- **representative** (주관적): 디자인 관점에서 어느 분기가 컴포넌트의 메인 사용
  케이스인가?
- 두 기준이 일치하면 결정 단순. 불일치 시 (예: superset 은 Selecting 인데
  representative 는 Completed) 별도 검토 필요. 이번 사례는 두 기준이 일치 (Selecting).

**§3.4 와의 차이**:
| | §3.4 | §3.5 |
|---|---|---|
| 흡수 관계 | 자식 → 부모 (다른 함수) | 분기 → 분기 (같은 함수) |
| 흡수 기준 | 부모가 시각 + 네비게이션 둘 다 제공 | 한 분기가 다른 분기의 시각 superset |
| 해소 | 자식 preview 제거 | subset 분기 preview 제거 |

**커밋**: (이번 commit)

---

## 4. 사례 추가 방법

새로운 시그널/사례가 발견되면 §3 에 사례를 추가한다.

각 사례는 다음 구조로 작성:

- **시그널**: preview 에서 발견된 어색함의 구체 증상.
- **원인**: 컴포넌트 구조/색상/책임 측면 분석.
- **해소**: 적용한 변경. self-contained 로 변환 / 책임 이전 / preview wrapper /
  preview 미부착 / 디자인 변경 등 가능한 옵션 중 어떤 것을 선택했고 왜 그랬는지.
- **커밋**: 해소를 적용한 커밋 해시 (있으면).

§2 의 진단 분기에 새 가지가 필요하면 그것도 함께 갱신한다.

---

## 5. 적용하지 않은 옵션의 기록

논의는 됐지만 채택하지 않은 옵션도 사례에 짧게 남겨 둔다 (왜 채택 안 했는지). 같은
시그널이 다음에 나타났을 때 의사결정의 근거가 된다.

예시 (§3.3):
- 옵션 A (디자인 변경, self-contained): selection 시각화 재설계 비용 큼 → 보류.
- 옵션 C (list 와 item 묶기): 큰 리팩토링 → 보류.
- 옵션 D (preview 미부착): 시각 단서 잃는 비용이 더 큼 → 보류.
