package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.onSurfaceVariant
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TableState
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimetableViewModel
import kotlinx.coroutines.launch

@Composable
fun ThemeDetailPage(
    timetableViewModel: TimetableViewModel = hiltViewModel(),
    userViewModel: UserViewModel = hiltViewModel(),
) {
    var themeName by remember { mutableStateOf("") }

    val table by timetableViewModel.currentTable.collectAsState()
    val trimParam by userViewModel.trimParam.collectAsState()
    val previewTheme by timetableViewModel.previewTheme.collectAsState()
    val tableState =
        TableState(table ?: TableDto.Default, trimParam, previewTheme)

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
                        },
                )
            },
            actions = {
                Text(
                    text = "저장",
                    style = SNUTTTypography.body1,
                    modifier = Modifier
                        .clicks {
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
            ) {
                EditText(
                    value = themeName,
                    onValueChange = { themeName = it },
                    enabled = true,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    underlineEnabled = false,
                    textStyle = SNUTTTypography.body1,
                )
            }
            SettingColumn(
                title = "색 조합",
            ) {
                ThemeDetailItem(
                    title = "색상1",
                ) {
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            SettingItem(
                title = "기본 테마로 지정",
                hasNextPage = false,
            ) {
                PoorSwitch(false)
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
    titleColor: Color = MaterialTheme.colors.onSurfaceVariant,
    modifier: Modifier = Modifier,
    actions: @Composable () -> Unit = {},
    content: @Composable () -> Unit = {}
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
