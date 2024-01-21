package com.wafflestudio.snutt2.views.logged_in.home.search.search_option

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.model.TagDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
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
        animationSpec = SearchOptionSheetConstants.AnimationSpec,
        label = "baseAnimatedFloat",
    )

    var normalSheetHeightPx = remember { 0 } // 태그 선택 sheet의 높이
    var maxSheetHeightPx = remember { 0 } // 시간대 선택 sheet의 높이
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
                modifier = Modifier.size(
                    width = constraints.maxWidth.toDp() - tagTypePlaceable.width.toDp(),
                    // tag column의 높이를 tagType column의 높이로 설정
                    height = tagTypePlaceable.height.toDp(),
                ),
                tagsByTagType = tagsByTagType,
                selectedTimes = draggedTimeBlock,
                baseAnimatedFloat = baseAnimatedFloat,
                onToggleTag = {
                    scope.launch {
                        if (it == TagDto.TIME_SELECT) {
                            if (tagsByTagType.first { it.item == TagDto.TIME_SELECT }.state.not() && draggedTimeBlock.value.all { it.all { it.not() } }) {
                                optionSheetMode = OptionSheetMode.TimeSelect
                            }
                        }
                        viewModel.toggleTag(it)
                    }
                },
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
                    // 확정 선택된 시간대(회색 밑줄로 뜨는 부분)가 없는 상태에서 취소를 누르면 태그 선택도 해제하기 (다시 누를 수 있게)
                    if (draggedTimeBlock.value.all { it.all { it.not() } }) {
                        scope.launch {
                            viewModel.toggleTag(TagDto.TIME_SELECT)
                        }
                    }
                },
                onConfirm = {
                    optionSheetMode = OptionSheetMode.Normal
                    scope.launch {
                        viewModel.setDraggedTimeBlock(it)
                        // 시간대를 하나도 선택을 안 하고 완료를 누르면 태그 선택도 해제하기 (다시 누를 수 있게)
                        if (it.all { it.all { it.not() } }) {
                            viewModel.toggleTag(TagDto.TIME_SELECT)
                        }
                    }
                },
            )
        }.first().measure(constraints)

        val confirmButtonPlaceable = subcompose(slotId = 4) {
            SearchOptionConfirmButton(baseAnimatedFloat, applyOption)
        }.first().measure(constraints)

        // 한번만 계산, 할당
        if (normalSheetHeightPx == 0 && maxSheetHeightPx == 0) {
            normalSheetHeightPx =
                tagTypePlaceable.height + SearchOptionSheetConstants.TopMargin.toPx()
                .roundToInt() + confirmButtonPlaceable.height
            maxSheetHeightPx = dragSheetPlaceable.height
        }

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
            if (baseAnimatedFloat.value != 0f) dragSheetPlaceable.placeRelative(0, 0)
        }
    }
}
