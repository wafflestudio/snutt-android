package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skydoves.colorpickerview.ColorEnvelope
import com.skydoves.colorpickerview.ColorPickerDialog
import com.skydoves.colorpickerview.flag.BubbleFlag
import com.skydoves.colorpickerview.flag.FlagMode
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener
import com.wafflestudio.snutt2.components.compose.CheckedIcon
import com.wafflestudio.snutt2.components.compose.ColorBox
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy
import com.wafflestudio.snutt2.R

@Composable
fun LectureColorSelectorPage(
    lectureDetailViewModel: LectureDetailViewModel = hiltViewModel(),
    timetableViewModel: TimetableViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val context = LocalContext.current

    val lectureState by lectureDetailViewModel.editingLectureDetail.collectAsState()
    val initialLectureColor = remember(Unit) { lectureState.color }

    val theme by timetableViewModel.tableTheme.collectAsState()

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.lecture_color_selector_page_app_bar_title),
        ) {
            navController.popBackStack()
        }

        Spacer(modifier = Modifier.height(10.dp))
        if (theme is CustomTheme) {
            (theme as CustomTheme).colors.forEachIndexed { idx, color ->
                ColorItem(
                    color = color,
                    title = "${theme.name} $idx",
                    isSelected = color == lectureState.color,
                    onClick = {
                        lectureDetailViewModel.editLectureDetail(
                            lectureState.copy(
                                color = color,
                            ),
                        )
                    },
                )
            }
            Column {
                ColorItem(
                    color = initialLectureColor,
                    title = stringResource(R.string.lecture_color_selector_page_custom_color),
                    isSelected = (theme as CustomTheme).colors.contains(lectureState.color).not(),
                    onClick = {
                        colorSelectorDialog(context, "글자 색")
                            .flatMap { fgColor ->
                                colorSelectorDialog(context, "배경 색").map { Pair(fgColor, it) }
                            }
                            .subscribeBy { (fgColor, bgColor) ->
                                lectureDetailViewModel.editLectureDetail(
                                    lectureState.copy(
                                        colorIndex = 0L,
                                        color = ColorDto(fgColor, bgColor),
                                    ),
                                )
                                navController.popBackStack()
                            }
                    },
                )
            }
        } else {
            for (idx in 1L..9L) ColorItem(
                color = ColorDto(
                    fgColor = 0xffffff,
                    bgColor = (theme as BuiltInTheme).getColorByIndex(context, idx),
                ),
                title = "${theme.name} $idx",
                isSelected = (idx == lectureState.colorIndex),
            ) {
                lectureDetailViewModel.editLectureDetail(
                    lectureState.copy(
                        colorIndex = idx,
                        color = ColorDto(),
                    ),
                )
                navController.popBackStack()
            }
            ColorItem(
                color = lectureState.color,
                title = stringResource(R.string.lecture_color_selector_page_custom_color),
                isSelected = (lectureState.colorIndex == 0L),
            ) {
                colorSelectorDialog(context, "글자 색")
                    .flatMap { fgColor ->
                        colorSelectorDialog(context, "배경 색").map { Pair(fgColor, it) }
                    }
                    .subscribeBy { (fgColor, bgColor) ->
                        lectureDetailViewModel.editLectureDetail(
                            lectureState.copy(
                                colorIndex = 0L,
                                color = ColorDto(fgColor, bgColor),
                            ),
                        )
                        navController.popBackStack()
                    }
            }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorDto,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .clicks { onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColorBox(color)
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(fontSize = 15.sp),
        )
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) CheckedIcon(modifier = Modifier.size(20.dp))
    }
}

// TODO: 언젠가는 compose로 직접...
fun colorSelectorDialog(context: Context, title: String): Maybe<Int> {
    return Maybe.create { emitter ->
        ColorPickerDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(
                "확인",
                object : ColorEnvelopeListener {
                    override fun onColorSelected(
                        envelope: ColorEnvelope?,
                        fromUser: Boolean,
                    ) {
                        envelope?.color?.let {
                            emitter.onSuccess(it)
                        }
                    }
                },
            )
            .attachAlphaSlideBar(false)
            .setOnDismissListener {
                emitter.onComplete()
            }
            .apply {
                val bubbleFlag = BubbleFlag(context)
                bubbleFlag.flagMode = FlagMode.ALWAYS
                colorPickerView.flagView = bubbleFlag
            }
            .show()
    }
}

@Preview
@Composable
fun LectureColorSelectorPagePreview() {
    LectureColorSelectorPage()
}
