package com.wafflestudio.snutt2.views.logged_in.home.drawer.refactor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.DrawerIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.LectureListIcon
import com.wafflestudio.snutt2.components.compose.NotificationIcon
import com.wafflestudio.snutt2.components.compose.RingingAlarmIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.lib.logging.AnalyticsScreen
import com.wafflestudio.snutt2.lib.logging.logImpression
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalAnalyticsLogger
import com.wafflestudio.snutt2.views.LocalCompactState
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.LocalHomePageController
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalRemoteConfig
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.home.BottomNavigation
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.showTitleChangeDialog
import com.wafflestudio.snutt2.views.logged_in.home.timetable.TimeTable

@Composable
fun TimetablePageTemp(
    uncheckedNotification: Boolean,
    onOpenDrawer: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val table = LocalTableState.current.table
    val tableTrimParam = LocalTableState.current.trimParam
    val remoteConfig = LocalRemoteConfig.current
    val composableStates = ComposableStatesWithScope(scope)
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val newSemesterNotify by tableListViewModel.newSemesterNotify.collectAsState(false)
    val vacancyNotificationBannerEnabled by remoteConfig.vacancyNotificationBannerEnabled.collectAsState(
        false,
    )
    val analyticsLogger = LocalAnalyticsLogger.current
    val pageController = LocalHomePageController.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
            .logImpression(AnalyticsScreen.TimetableHome),
    ) {
        TopBar(
            title = {
                Text(
                    text = table.title,
                    style = SNUTTTypography.h2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clicks {
                            showTitleChangeDialog(
                                table.title,
                                table.id,
                                composableStates,
                                tableListViewModel::changeTableName,
                            )
                        },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.timetable_credit,
                        getCreditSumFromLectureList(table.lectureList),
                    ),
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    color = SNUTTColors.Gray200,
                )
            },
            navigationIcon = {
                IconWithAlertDot(newSemesterNotify) { centerAlignedModifier ->
                    DrawerIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { onOpenDrawer() },
                    )
                }
            },
            actions = {
                LectureListIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.LecturesOfTable) },
                )
                IconWithAlertDot(uncheckedNotification) { centerAlignedModifier ->
                    NotificationIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { navController.navigate(NavigationDestination.Notification) },
                        colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                    )
                }
            },
        )
        if (vacancyNotificationBannerEnabled) {
            VacancyBanner(
                onClick = {
                    navController.navigate(NavigationDestination.VacancyNotification)
                },
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            TimeTable(
                table = LocalTableState.current.table,
                trimParam = LocalTableState.current.trimParam,
                tableLectureCustomOptions = LocalTableState.current.tableLectureCustomOptions,
                previewTheme = LocalTableState.current.previewTheme,
                compactMode = LocalCompactState.current,
                navigator = LocalNavController.current,
                selectedLecture = null,
            )
        }

        BottomNavigation(
            pageState = pageController.homePageState.value,
            onUpdatePageState = { pageController.update(it) },
        )
    }
}

@Composable
fun VacancyBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(SNUTTColors.BannerBlue)
            .clicks { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RingingAlarmIcon(
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            Text(
                text = stringResource(R.string.vacancy_banner_text),
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.AllWhite,
                ),
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "NEW",
                style = SNUTTTypography.h5.copy(
                    fontSize = 8.sp,
                    color = SNUTTColors.AllWhite,
                ),
            )
        }
    }
}
