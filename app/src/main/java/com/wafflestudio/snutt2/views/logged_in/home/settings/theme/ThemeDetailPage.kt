package com.wafflestudio.snutt2.views.logged_in.home.settings.theme

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.CloseIcon
import com.wafflestudio.snutt2.components.compose.ColorBox
import com.wafflestudio.snutt2.components.compose.ColorCircle
import com.wafflestudio.snutt2.components.compose.DuplicateIcon
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.Switch
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalModalState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingColumn
import com.wafflestudio.snutt2.views.logged_in.home.settings.SettingItem
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.colorSelectorDialog
import io.reactivex.rxjava3.kotlin.subscribeBy
import kotlinx.coroutines.launch

@Composable
fun ThemeDetailPage(
    onClickSave: suspend () -> Unit = {},
    themeDetailViewModel: ThemeDetailViewModel = hiltViewModel(),
    timetableViewModel: TimetableViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val navController = LocalNavController.current
    val modalState = LocalModalState.current

    val table by timetableViewModel.currentTable.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val tableState =
        TableState(table ?: TableDto.Default, trimParam, previewTheme)

    val editingTheme by themeDetailViewModel.editingTheme.collectAsState()
    val themeColors by themeDetailViewModel.themeColors.collectAsState()
    var themeName by remember { mutableStateOf(editingTheme.name) }
    var isDefault by remember { mutableStateOf(editingTheme.isDefault) }

    val onBackPressed: () -> Unit = {
        navController.popBackStack()
    }

    val handleSaveTheme: () -> Unit = {
        scope.launch {
            themeDetailViewModel.updateThemeName(themeName)
            themeDetailViewModel.updateIsDefault(isDefault)
            if (editingTheme.id == 0L) {
                themeDetailViewModel.createCustomTheme()
            } else {
                themeDetailViewModel.updateCustomTheme()
            }
            onClickSave()
            navController.popBackStack()
        }
    }

    BackHandler {
        onBackPressed()
    }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.95f)
            .fillMaxWidth(),
    ) {
        TopBar(
            title = {
                Text(
                    text = "커스텀 테마",
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            navigationIcon = {
                Text(
                    text = "취소",
                    style = SNUTTTypography.body1,
                    modifier = Modifier
                        .clicks {
                            onBackPressed()
                        },
                )
            },
            actions = {
                Text(
                    text = "저장",
                    style = SNUTTTypography.body1,
                    modifier = Modifier
                        .clicks {
                            if (isDefault != editingTheme.isDefault) {
                                modalState.setOkCancel(
                                    context = context,
                                    title = if (isDefault) {
                                        "디폴트 테마 지정"
                                    } else {
                                        "디폴트 테마 해제"
                                    },
                                    onConfirm = {
                                        handleSaveTheme()
                                        modalState.hide()
                                    },
                                    onDismiss = {
                                        modalState.hide()
                                    },
                                    content = {
                                        Text(
                                            text = if (isDefault) {
                                                context.getString(R.string.custom_theme_dialog_set_as_default)
                                            } else {
                                                context.getString(R.string.custom_theme_dialog_unset_default)
                                            },
                                            style = SNUTTTypography.body1,
                                        )
                                    },
                                ).show()
                            } else {
                                handleSaveTheme()
                            }
                        },
                )
            },
        )
        Column(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            ThemeDetailItem(
                title = "테마명",
                titleColor = MaterialTheme.colors.onSurfaceVariant.copy(alpha = if (editingTheme.isCustom) 1f else 0.5f),
            ) {
                EditText(
                    value = themeName,
                    onValueChange = { themeName = it },
                    enabled = editingTheme.isCustom,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    underlineEnabled = false,
                    textStyle = SNUTTTypography.body1.copy(
                        color = if (editingTheme.isCustom) {
                            MaterialTheme.colors.onSurface
                        } else {
                            MaterialTheme.colors.onSurfaceVariant.copy(
                                alpha = 0.5f,
                            )
                        },
                    ),
                )
            }
            SettingColumn(
                title = "색 조합",
            ) {
                if (editingTheme.isCustom) {
                    themeColors.forEachIndexed { idx, colorWithExpanded ->
                        val state = remember {
                            MutableTransitionState(false).apply { targetState = true }
                        }
                        AnimatedVisibility(state) {
                            Column {
                                ThemeDetailItem(
                                    title = "색상${idx + 1}",
                                    modifier = Modifier.clicks {
                                        themeDetailViewModel.toggleColorExpanded(idx)
                                    },
                                    actions = {
                                        DuplicateIcon(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clicks {
                                                    themeDetailViewModel.duplicateColor(idx)
                                                },
                                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurfaceVariant),
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        CloseIcon(
                                            modifier = Modifier
                                                .size(30.dp)
                                                .clicks {
                                                    themeDetailViewModel.removeColor(idx)
                                                },
                                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onSurfaceVariant),
                                        )
                                    },
                                ) {
                                    ColorBox(0, colorWithExpanded.item, previewTheme)
                                }
                                AnimatedVisibility(visible = colorWithExpanded.state) {
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
                                                    text = "글꼴색",
                                                    color = MaterialTheme.colors.onSurfaceVariant,
                                                    style = SNUTTTypography.body2,
                                                )
                                                Spacer(modifier = Modifier.width(11.dp))
                                                ColorCircle(
                                                    color = colorWithExpanded.item.fgColor
                                                        ?: 0xffffff,
                                                    modifier = Modifier
                                                        .size(25.dp)
                                                        .clicks {
                                                            colorSelectorDialog(
                                                                context,
                                                                "글꼴색",
                                                            ).subscribeBy {
                                                                themeDetailViewModel.updateColor(
                                                                    idx,
                                                                    it,
                                                                    colorWithExpanded.item.bgColor
                                                                        ?: 0xffffff,
                                                                )
                                                            }
                                                        },
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Row {
                                                Text(
                                                    text = "배경색",
                                                    color = MaterialTheme.colors.onSurfaceVariant,
                                                    style = SNUTTTypography.body2,
                                                )
                                                Spacer(modifier = Modifier.width(11.dp))
                                                ColorCircle(
                                                    color = colorWithExpanded.item.bgColor
                                                        ?: 0xffffff,
                                                    modifier = Modifier
                                                        .size(25.dp)
                                                        .clicks {
                                                            colorSelectorDialog(
                                                                context,
                                                                "배경색",
                                                            ).subscribeBy {
                                                                themeDetailViewModel.updateColor(
                                                                    idx,
                                                                    colorWithExpanded.item.fgColor
                                                                        ?: 0xffffff,
                                                                    it,
                                                                )
                                                            }
                                                        },
                                                )
                                            }
                                        }
                                    }
                                }
                                Divider(thickness = 0.5.dp, color = MaterialTheme.colors.background)
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clicks {
                                themeDetailViewModel.addColor()
                            },
                    ) {
                        Text(
                            text = "+ 색상 추가",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colors.onBackground,
                        )
                    }
                } else {
                    (1..9).forEach { idx ->
                        ThemeDetailItem(
                            title = "색상${idx + 1}",
                            titleColor = MaterialTheme.colors.onSurfaceVariant.copy(alpha = 0.5f),
                        ) {
                            ColorBox(
                                lectureColorIndex = idx.toLong(),
                                lectureColor = null,
                                theme = editingTheme,
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingItem(
                title = "기본 테마로 지정",
                hasNextPage = false,
            ) {
                Switch(
                    checked = isDefault,
                    onCheckChanged = { isDefault = it },
                )
            }
            SettingColumn(
                title = "미리보기",
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SNUTTColors.White)
                        .padding(15.dp)
                        .size(
                            (LocalConfiguration.current.screenWidthDp * 0.8).dp,
                            (LocalConfiguration.current.screenHeightDp * 0.6).dp,
                        )
                        .align(Alignment.CenterHorizontally),
                ) {
                    CompositionLocalProvider(LocalTableState provides tableState) {
                        TimeTable(selectedLecture = null, touchEnabled = false)
                    }
                }
            }
        }
    }
}

@Composable
fun ThemeDetailItem(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colors.onSurfaceVariant,
    actions: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(MaterialTheme.colors.surface)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            modifier = Modifier.width(60.dp),
            style = SNUTTTypography.body1.copy(
                color = titleColor,
            ),
        )
        content()
        Spacer(modifier = Modifier.weight(1f))
        actions()
    }
}
