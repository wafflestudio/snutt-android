package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class OptionSheetMode {
    Normal, TimeSelect,
}

@Composable
fun SearchOptionSheet(
    applyOption: () -> Unit,
    draggedTimeBlock: State<List<List<Boolean>>>,
) {
    val viewModel = hiltViewModel<SearchViewModel>()
    val tagsByTagType by viewModel.tagsByTagType.collectAsState()
    val selectedTagType by viewModel.selectedTagType.collectAsState()
    val scope = rememberCoroutineScope()

    var optionSheetMode by remember {
        mutableStateOf(OptionSheetMode.Normal)
    }

    // 전환 애니메이션에서 베이스가 되는 float 값 (일반 모드일 때 0f, 시간대 선택 모드일 때 1f)
    val baseAnimatedFloat = animateFloatAsState(
        targetValue = when (optionSheetMode) {
            OptionSheetMode.Normal -> 0f
            OptionSheetMode.TimeSelect -> 1f
        },
        animationSpec = SearchOptionSheetConstants.SearchOptionSheetAnimationSpec,
        label = "baseAnimatedFloat",
    )

    var normalSheetHeightPx = remember { 0 }
    var maxSheetHeightPx = remember { 0 }
    val sheetHeightAnimatedPx = remember {
        derivedStateOf {
            (normalSheetHeightPx + baseAnimatedFloat.value * (maxSheetHeightPx - normalSheetHeightPx)).roundToInt()
        }
    }

    SubcomposeLayout(
        modifier = Modifier.background(SNUTTColors.White900),
    ) { constraints ->
        val tagTypePlaceable = subcompose(slotId = 1) {
            TagTypeColumn(
                selectedTagType = selectedTagType,
                baseAnimatedFloat = baseAnimatedFloat,
            ) {
                scope.launch {
                    viewModel.setTagType(it)
                }
            }
        }.first().measure(constraints)

        val tagListPlaceable = subcompose(slotId = 2) {
            TagsColumn(
                tagsByTagType = tagsByTagType,
                selectedTimes = draggedTimeBlock,
                baseAnimatedFloat = baseAnimatedFloat,
                // tag column의 높이를 tagType column의 높이로 설정
                height = tagTypePlaceable.height.toDp(),
                width = constraints.maxWidth.toDp() - tagTypePlaceable.width.toDp(),
                toggleTimeSelectTagTo = {
                    optionSheetMode = if (it) {
                        OptionSheetMode.TimeSelect
                    } else {
                        OptionSheetMode.Normal
                    }
                    scope.launch {
                        viewModel.toggleTimeSpecificSearchTag(it)
                    }
                },
                onToggleTag = {
                    scope.launch {
                        viewModel.toggleTag(it)
                    }
                },
            )
        }.first().measure(constraints)

        val dragSheetPlaceable = subcompose(slotId = 3) {
            TimeSelectSheet(
                basedAnimatedFloat = baseAnimatedFloat,
                initialDraggedTimeBlock = draggedTimeBlock,
                onCancel = {
                    optionSheetMode = OptionSheetMode.Normal
                    scope.launch {
                        viewModel.toggleTimeSpecificSearchTag(draggedTimeBlock.value.any { it.any { it } })
                    }
                },
                onConfirm = {
                    optionSheetMode = OptionSheetMode.Normal
                    scope.launch {
                        viewModel.setDraggedTimeBlock(it)
                        val timeSelected = it.any { it.any { it } }
                        viewModel.toggleTimeSpecificSearchTag(timeSelected)
                    }
                },
            )
        }.first().measure(constraints)

        val confirmButtonPlaceable = subcompose(slotId = 4) {
            SearchOptionConfirmButton(baseAnimatedFloat, applyOption)
        }.first().measure(constraints)

        normalSheetHeightPx =
            tagTypePlaceable.height + 40.dp.toPx().roundToInt() + confirmButtonPlaceable.height
        maxSheetHeightPx = dragSheetPlaceable.height
        layout(
            tagTypePlaceable.width,
            sheetHeightAnimatedPx.value,
        ) {
            tagTypePlaceable.placeRelative(
                0,
                SearchOptionSheetConstants.SheetTopMargin.toPx().roundToInt(),
            )
            tagListPlaceable.placeRelative(
                tagTypePlaceable.width,
                SearchOptionSheetConstants.SheetTopMargin.toPx().roundToInt(),
            )
            confirmButtonPlaceable.placeRelative(
                0,
                tagTypePlaceable.height + SearchOptionSheetConstants.SheetTopMargin.toPx()
                    .roundToInt(),
            )
            if (baseAnimatedFloat.value != 0f) dragSheetPlaceable.placeRelative(0, 0)
        }
    }
}
