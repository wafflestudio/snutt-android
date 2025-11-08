package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.ExitIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.Selectable
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.compose.BottomSheetLoggingEffect
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.model.TagType
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import kotlin.math.roundToInt
import androidx.compose.runtime.State

private enum class OptionSheetMode {
    Normal, TimeSelect,
}

@Composable
fun SearchOptionSheet(
    tagsByTagType: State<List<Selectable<TagDto>>>,
    selectedTagType: State<TagType>,
    recentSearchedDepartments: State<List<Selectable<TagDto>>>,
    tagTypesNotEmpty: State<List<TagType>>,
    draggedTimeBlock: State<List<List<Boolean>>>,
    onSelectTagType: (TagType) -> Unit,
    onToggleTag: (TagDto) -> Unit,
    onRemoveRecent: (TagDto) -> Unit,
    onTimeSelectCancel: () -> Unit,
    onTimeSelectConfirm: (List<List<Boolean>>) -> Unit,
    applyOption: () -> Unit,
    hideBottomSheet: () -> Unit,
) {
    val bottomSheet = LocalBottomSheetState.current

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

    var normalSheetHeightPx by remember { mutableStateOf(0) } // 태그 선택 sheet의 높이
    var maxSheetHeightPx by remember { mutableStateOf(0) } // 시간대 선택 sheet의 높이
    val sheetHeightAnimatedPx = remember {
        derivedStateOf {
            // 태그 선택 sheet의 높이 ~ 시간대 선택 sheet의 높이까지 baseAnimatedFloat에 따라 변하는 값
            (normalSheetHeightPx + baseAnimatedFloat.value * (maxSheetHeightPx - normalSheetHeightPx)).roundToInt()
        }
    }

    BottomSheetLoggingEffect(
        bottomSheetState = bottomSheet,
        analyticsScreen = AnalyticsScreen.SearchFilter,
    )

    SubcomposeLayout(
        modifier = Modifier.background(SNUTTColors.White900),
    ) { constraints ->
        val tagTypePlaceable = subcompose(slotId = 1) {
            TagTypeColumn(
                tagTypesNotEmpty = tagTypesNotEmpty.value,
                selectedTagType = selectedTagType.value,
                baseAnimatedFloat = baseAnimatedFloat,
                onSelectTagType = onSelectTagType,
            )
        }.first().measure(constraints)

        val tagListPlaceable = subcompose(slotId = 2) {
            TagsColumn(
                modifier = Modifier.size(
                    width = constraints.maxWidth.toDp() - tagTypePlaceable.width.toDp(),
                    // tag column의 높이를 tagType column의 높이로 설정
                    height = tagTypePlaceable.height.toDp(),
                ),
                recentSearchedDepartments = recentSearchedDepartments.value,
                tagsByTagType = tagsByTagType.value,
                selectedTimes = draggedTimeBlock,
                baseAnimatedFloat = baseAnimatedFloat,
                onToggleTag = { tagDto ->
                    if (tagDto == TagDto.TIME_SELECT) {
                        if (tagsByTagType.value.first { it.item == TagDto.TIME_SELECT }.state.not() && draggedTimeBlock.value.all { it.all { it.not() } }) {
                            optionSheetMode = OptionSheetMode.TimeSelect
                        }
                    }
                    onToggleTag(tagDto)
                },
                onRemoveRecent = onRemoveRecent,
                openTimeSelectSheet = {
                    optionSheetMode = OptionSheetMode.TimeSelect
                },
            )
        }.first().measure(constraints)

        val dragSheetPlaceable = subcompose(slotId = 3) {
            TimeSelectSheet(
                basedAnimatedFloat = baseAnimatedFloat,
                initialDraggedTimeBlock = draggedTimeBlock,
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
