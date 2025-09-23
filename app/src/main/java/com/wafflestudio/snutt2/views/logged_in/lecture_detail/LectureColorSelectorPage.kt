package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CheckedIcon
import com.wafflestudio.snutt2.components.compose.ColorBox
import com.wafflestudio.snutt2.components.compose.ColorCircle
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.components.compose.showColorPickerDialog
import com.wafflestudio.snutt2.domainmodel.BuiltInColor
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomColor
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.LectureColor
import com.wafflestudio.snutt2.domainmodel.toCustomColor
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalModalState
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun LectureColorSelectorPage(
    lectureDetailViewModel: LectureDetailViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val modalState = LocalModalState.current

    val lectureState by lectureDetailViewModel.editingLectureDetail.collectAsState()

    val theme by lectureDetailViewModel.currentTableTheme.collectAsState()
    var customFgColor by remember {
        mutableStateOf(
            Color(
                lectureState.color.fgColor?.toLong() ?: 0xffffffff,
            ),
        )
    }
    var customBgColor by remember {
        mutableStateOf(
            Color(
                lectureState.color.bgColor?.toLong() ?: 0xffffffff,
            ),
        )
    }
    val isDarkMode = isDarkMode()

    var selectedIndex by remember { // -1: 커스텀 색상.  0,1,2...: 선택된 색상의 0-based 인덱스
        if (theme is CustomTheme) {
            mutableIntStateOf(
                theme.getColors(isDarkMode).indexOf(lectureState.color.toCustomColor()),
            )
        } else {
            mutableIntStateOf(lectureState.colorIndex.toInt() - 1)
        }
    }

    val onBackPressed = { // 뒤로가기 시 강의 색상을 selectedIndex에 해당하는 색상으로 변경
        lectureDetailViewModel.editLectureDetail(
            if (theme is CustomTheme) {
                lectureState.copy(
                    colorIndex = 0,
                    color = if (selectedIndex == -1) {
                        CustomColor(customFgColor, customBgColor).toColorDto()
                    } else {
                        theme.getColors(isDarkMode)[selectedIndex].toColorDto()
                    },
                )
            } else {
                lectureState.copy(
                    colorIndex = selectedIndex.toLong() + 1,
                    color = if (selectedIndex == -1) {
                        CustomColor(customFgColor, customBgColor).toColorDto()
                    } else {
                        CustomColor(Color.White, Color.White).toColorDto()
                    },
                )
            },
        )
        navController.popBackStack()
    }

    BackHandler {
        onBackPressed()
    }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.lecture_color_selector_page_app_bar_title),
        ) {
            onBackPressed()
        }
        Spacer(modifier = Modifier.height(10.dp))
        if (theme is CustomTheme) {
            theme.getColors(isDarkMode).forEachIndexed { idx, color ->
                ColorItem(
                    color = color,
                    title = "${theme.name} ${idx + 1}",
                    isSelected = idx == selectedIndex,
                    onClick = {
                        selectedIndex = idx
                    },
                )
            }
        } else {
            for (colorIndex in 1L..9L) ColorItem(
                color = BuiltInColor(
                    foreground = Color(0xffffffff),
                    background = (theme as BuiltInTheme).getColorByIndex(colorIndex),
                    colorIndex,
                ),
                title = "${theme.name} $colorIndex",
                isSelected = colorIndex.toInt() - 1 == selectedIndex,
            ) {
                selectedIndex = colorIndex.toInt() - 1
            }

            Column {
                ColorItem(
                    color = CustomColor(customFgColor, customBgColor),
                    title = stringResource(R.string.lecture_color_selector_page_custom_color),
                    isSelected = selectedIndex == -1,
                    onClick = {
                        selectedIndex = -1
                    },
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colors.surface),
                ) {
                    Spacer(modifier = Modifier.width(92.dp))
                    Column(
                        modifier = Modifier.padding(top = 5.dp, bottom = 12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = stringResource(R.string.theme_detail_color_fg),
                                color = MaterialTheme.colors.onSurfaceVariant,
                                style = SNUTTTypography.body2,
                            )
                            Spacer(modifier = Modifier.width(11.dp))
                            ColorCircle(
                                color = customFgColor,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clicks {
                                        showColorPickerDialog(
                                            context = context,
                                            modalState = modalState,
                                            initialColor = customFgColor,
                                            onColorPicked = { color ->
                                                customFgColor = color
                                                selectedIndex = -1 // 커스텀 색상을 변경하면 자동으로 커스텀을 선택
                                            },
                                        )
                                    },
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Text(
                                text = stringResource(R.string.theme_detail_color_bg),
                                color = MaterialTheme.colors.onSurfaceVariant,
                                style = SNUTTTypography.body2,
                            )
                            Spacer(modifier = Modifier.width(11.dp))
                            ColorCircle(
                                color = customBgColor,
                                modifier = Modifier
                                    .size(25.dp)
                                    .clicks {
                                        showColorPickerDialog(
                                            context = context,
                                            modalState = modalState,
                                            initialColor = customBgColor,
                                            onColorPicked = { color ->
                                                customBgColor = color
                                                selectedIndex = -1
                                            },
                                        )
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorItem(
    color: LectureColor,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colors.surface)
            .clicks { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ColorBox(color)
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = SNUTTTypography.body1,
        )
        if (isSelected) {
            CheckedIcon(
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500),
            )
        } else {
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}

@Preview
@Composable
fun LectureColorSelectorPagePreview() {
    LectureColorSelectorPage()
}
