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
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.kotlin.subscribeBy

@Composable
fun LectureColorSelectorPage() {
    val navController = LocalNavController.current
    val context = LocalContext.current

    // share viewModel
    val backStackEntry = remember(navController.currentBackStackEntry) {
        navController.getBackStackEntry(NavigationDestination.Home)
    }
    val vm = hiltViewModel<LectureDetailViewModel>(backStackEntry)

    val lectureState by vm.editingLectureDetail.collectAsState()

    val currentTable by vm.currentTable.collectAsState()
    val tableColorTheme = currentTable?.theme ?: TimetableColorTheme.SNUTT

    Column(modifier = Modifier.background(SNUTTColors.White900)) {
        SimpleTopBar(
            title = "강의 색상 선택하기"
        ) {
            navController.popBackStack()
        }

        Spacer(modifier = Modifier.height(10.dp))
        for (idx in 1L..9L) ColorItem(
            color = null,
            index = idx,
            theme = tableColorTheme,
            isSelected = (idx == lectureState.colorIndex)
        ) {
            vm.editLectureDetail(lectureState.copy(colorIndex = idx, color = ColorDto()))
            navController.popBackStack()
        }
        ColorItem(
            color = lectureState.color,
            index = 0,
            theme = tableColorTheme,
            isSelected = (lectureState.colorIndex == 0L)
        ) {
            colorSelectorDialog(context, "글자 색")
                .flatMap { fgColor ->
                    colorSelectorDialog(context, "배경 색").map { Pair(fgColor, it) }
                }
                .subscribeBy { (fgColor, bgColor) ->
                    vm.editLectureDetail(
                        lectureState.copy(
                            colorIndex = 0L,
                            color = ColorDto(fgColor, bgColor)
                        )
                    )
                    navController.popBackStack()
                }
        }
    }
}

@Composable
fun ColorItem(
    color: ColorDto?,
    index: Long,
    theme: TimetableColorTheme,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(40.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        ColorBox(lectureColor = color, lectureColorIndex = index, theme = theme)
        Spacer(modifier = Modifier.width(20.dp))
        if (index != 0L) Text(text = "SNUTT$index", style = SNUTTTypography.body1.copy(fontSize = 15.sp))
        else Text(text = "커스텀", style = SNUTTTypography.body1.copy(fontSize = 15.sp))
        Spacer(modifier = Modifier.weight(1f))
        if (isSelected) CheckedIcon(modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(20.dp))
    }
}

// TODO: 언젠가는 compose로 직접...
private fun colorSelectorDialog(context: Context, title: String): Maybe<Int> {
    return Maybe.create { emitter ->
        ColorPickerDialog.Builder(context)
            .setTitle(title)
            .setPositiveButton(
                "확인",
                object : ColorEnvelopeListener {
                    override fun onColorSelected(
                        envelope: ColorEnvelope?,
                        fromUser: Boolean
                    ) {
                        envelope?.color?.let {
                            emitter.onSuccess(it)
                        }
                    }
                }
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
