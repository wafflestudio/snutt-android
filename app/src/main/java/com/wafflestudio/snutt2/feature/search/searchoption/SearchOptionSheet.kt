package com.wafflestudio.snutt2.feature.search.searchoption

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchTag
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.lib.DataWithState
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.ui.components.compose.ExitIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import kotlin.math.roundToInt

private enum class OptionSheetMode {
    Normal,
    TimeSelect,
}

@Composable
fun SearchOptionSheet(
    searchTags: List<Selectable<SearchTag>>,
    tagTypes: List<TagType>,
    selectedTagType: TagType,
    recentSearchedDepartments: List<Selectable<SearchTag>>,
    draggedTimeBlock: List<List<Boolean>>,
    currentTableLectures: List<LocalLecture>,
    tableLectureCustomOptions: TableLectureCustom,
    onSelectTagType: (TagType) -> Unit,
    onToggleTag: (SearchTag) -> Unit,
    onRemoveRecentSearchedDepartments: (SearchTag) -> Unit,
    onTimeSelectCancel: () -> Unit,
    onTimeSelectConfirm: (List<List<Boolean>>) -> Unit,
    applyOption: () -> Unit,
    hideBottomSheet: () -> Unit,
) {
    var optionSheetMode by remember {
        mutableStateOf(OptionSheetMode.Normal)
    }

    // 전환 애니메이션에서 베이스가 되는 float 값 (일반 모드일 때 0f, 시간대 선택 모드일 때 1f)
    val baseAnimatedFloat = animateFloatAsState(
        targetValue = when (optionSheetMode) {
            OptionSheetMode.Normal -> 0f
            OptionSheetMode.TimeSelect -> 1f
        },
        animationSpec = SearchOptionSheetConstants.AnimationSpec,
        label = "baseAnimatedFloat",
    )
    val alphaAnimatedFloat = 1f - baseAnimatedFloat.value
    val offsetXAnimatedDp = (LocalWindowInfo.current.containerSize.width.dp.value - SearchOptionSheetConstants.TAG_COLUMN_WIDTH_DP).dp * baseAnimatedFloat.value

    var normalSheetHeightPx by remember { mutableIntStateOf(0) }
    var maxSheetHeightPx by remember { mutableIntStateOf(0) }
    val sheetHeightAnimatedPx = remember {
        derivedStateOf {
            // 태그 선택 sheet의 높이 ~ 시간대 선택 sheet의 높이까지 baseAnimatedFloat에 따라 변하는 값
            (normalSheetHeightPx + baseAnimatedFloat.value * (maxSheetHeightPx - normalSheetHeightPx)).roundToInt()
        }
    }

    SubcomposeLayout(
        modifier = Modifier.background(SNUTTColors.White900),
    ) { constraints ->
        val tagTypePlaceable = subcompose(slotId = 1) {
            TagTypeColumn(
                tagTypesNotEmpty = tagTypes,
                selectedTagType = selectedTagType,
                baseAnimatedFloat = baseAnimatedFloat,
                onSelectTagType = onSelectTagType,
            )
        }.first().measure(constraints)

        val tagListPlaceable = subcompose(slotId = 2) {
            SearchTagsColumn(
                modifier = Modifier
                    .size(
                        width = constraints.maxWidth.toDp() - tagTypePlaceable.width.toDp(),
                        // tag column의 높이를 tagType column의 높이로 설정
                        height = tagTypePlaceable.height.toDp(),
                    )
                    .offset(x = offsetXAnimatedDp)
                    .alpha(alphaAnimatedFloat),
                recentSearchedDepartments = recentSearchedDepartments,
                searchTags = searchTags,
                selectedTimes = draggedTimeBlock,
                onToggleTag = { searchTag ->
                    if (searchTag == SearchTag.TimeSelect) {
                        if (searchTags.first { it.item == SearchTag.TimeSelect }.state.not() && draggedTimeBlock.all { it.all { it.not() } }) {
                            optionSheetMode = OptionSheetMode.TimeSelect
                        }
                    }
                    onToggleTag(searchTag)
                },
                onRemoveRecentSearchedDepartment = onRemoveRecentSearchedDepartments,
                openTimeSelectSheet = {
                    optionSheetMode = OptionSheetMode.TimeSelect
                },
            )
        }.first().measure(constraints)

        val dragSheetPlaceable = subcompose(slotId = 3) {
            TimeSelectSheet(
                modifier = Modifier.alpha(baseAnimatedFloat.value),
                backHandlerEnabled = optionSheetMode == OptionSheetMode.TimeSelect,
                initialDraggedTimeBlock = draggedTimeBlock,
                currentTableLectures = currentTableLectures,
                tableLectureCustomOptions = tableLectureCustomOptions,
                onCancel = {
                    optionSheetMode = OptionSheetMode.Normal
                    onTimeSelectCancel()
                },
                onConfirm = {
                    optionSheetMode = OptionSheetMode.Normal
                    onTimeSelectConfirm(it)
                },
            )
        }.first().measure(constraints)

        val confirmButtonPlaceable = subcompose(slotId = 4) {
            SearchOptionConfirmButton(baseAnimatedFloat, applyOption)
        }.first().measure(constraints)

        val closeBottomSheetPlaceable = subcompose(slotId = 5) {
            Row(
                modifier = Modifier.clicks { hideBottomSheet() },
            ) {
                ExitIcon()
            }
        }.first().measure(constraints)

        normalSheetHeightPx =
            tagTypePlaceable.height + SearchOptionSheetConstants.TopMargin.toPx()
                .roundToInt() + confirmButtonPlaceable.height
        maxSheetHeightPx = dragSheetPlaceable.height

        layout(
            width = tagTypePlaceable.width + tagListPlaceable.width,
            height = sheetHeightAnimatedPx.value,
        ) {
            tagTypePlaceable.placeRelative(
                0,
                SearchOptionSheetConstants.TopMargin.toPx().roundToInt(),
            )
            tagListPlaceable.placeRelative(
                tagTypePlaceable.width,
                SearchOptionSheetConstants.TopMargin.toPx().roundToInt(),
            )
            confirmButtonPlaceable.placeRelative(
                0,
                tagTypePlaceable.height + SearchOptionSheetConstants.TopMargin.toPx()
                    .roundToInt(),
            )
            if (optionSheetMode != OptionSheetMode.TimeSelect) { // 시간 선택 시트에서는 X 버튼 숨김
                closeBottomSheetPlaceable.placeRelative(
                    tagTypePlaceable.width + tagListPlaceable.width - 52.dp.toPx().roundToInt(),
                    (SearchOptionSheetConstants.TopMargin.toPx().roundToInt() - 32.dp.toPx().roundToInt()) / 2,
                )
            }
            if (optionSheetMode == OptionSheetMode.TimeSelect) dragSheetPlaceable.placeRelative(0, 0)
        }
    }
}

private fun previewSheet(
    selectedTagType: TagType,
    selectedTags: List<SearchTag> = emptyList(),
    recentSearchedDepartments: List<SearchTag> = emptyList(),
    draggedTimeBlock: List<List<Boolean>> = TableTrimParam.TimeBlockGridDefault,
) = @Composable {
    SearchOptionSheet(
        searchTags = previewAllTags.filter { it.type == selectedTagType }.map { DataWithState(it, selectedTags.contains(it)) },
        tagTypes = previewTagTypes,
        selectedTagType = selectedTagType,
        recentSearchedDepartments = recentSearchedDepartments.map { DataWithState(it, selectedTags.contains(it)) },
        draggedTimeBlock = draggedTimeBlock,
        currentTableLectures = emptyList(),
        tableLectureCustomOptions = TableLectureCustom.Default,
        onSelectTagType = {},
        onToggleTag = {},
        onRemoveRecentSearchedDepartments = {},
        onTimeSelectCancel = {},
        onTimeSelectConfirm = {},
        applyOption = {},
        hideBottomSheet = {},
    )
}

// 기본 상태: 정렬 기준 탭
@Preview
@Composable
private fun SearchOptionSheetPreview_SortCriteria() {
    previewSheet(selectedTagType = TagType.SORT_CRITERIA)()
}

// 태그 선택된 상태: 분류 탭에서 "전공필수" 선택
@Preview
@Composable
private fun SearchOptionSheetPreview_ClassificationSelected() {
    previewSheet(
        selectedTagType = TagType.CLASSIFICATION,
        selectedTags = listOf(SearchTag.Regular(TagType.CLASSIFICATION, "전공필수")),
    )()
}

// 학과 탭 + 최근 검색 기록 존재
@Preview
@Composable
private fun SearchOptionSheetPreview_DepartmentWithRecent() {
    previewSheet(
        selectedTagType = TagType.DEPARTMENT,
        recentSearchedDepartments = listOf(
            SearchTag.Regular(TagType.DEPARTMENT, "컴퓨터공학부"),
            SearchTag.Regular(TagType.DEPARTMENT, "전기정보공학부"),
        ),
    )()
}

// 시간대 탭 + 일부 시간 블록 선택됨
@Preview
@Composable
private fun SearchOptionSheetPreview_TimeWithBlocks() {
    previewSheet(
        selectedTagType = TagType.TIME,
        selectedTags = listOf(SearchTag.TimeSelect),
        draggedTimeBlock = TableTrimParam.TimeBlockGridDefault.mapIndexed { dayIdx, day ->
            day.mapIndexed { slotIdx, _ -> dayIdx == 0 && slotIdx in 4..9 }
        },
    )()
}
