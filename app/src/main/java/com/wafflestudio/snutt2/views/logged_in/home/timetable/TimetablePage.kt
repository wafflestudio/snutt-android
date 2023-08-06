package com.wafflestudio.snutt2.views.logged_in.home.timetable

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BookmarkPageIcon
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.DrawerIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.LectureListIcon
import com.wafflestudio.snutt2.components.compose.ShareIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.TrashIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.lib.shareScreenshotFromView
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.RNModuleActivity
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.PoorSwitch
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.showTitleChangeDialog
import com.wafflestudio.snutt2.views.logged_in.vacancy_noti.VacancyViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun TimetablePage() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val table = LocalTableState.current.table
    val remoteConfig = LocalRemoteConfig.current
    val composableStates = ComposableStatesWithScope(scope)
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val userViewModel = hiltViewModel<UserViewModel>()
    val vacancyViewModel = hiltViewModel<VacancyViewModel>()
    val newSemesterNotify by tableListViewModel.newSemesterNotify.collectAsState(false)
    val firstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()
    val vacancyBannerOpened by vacancyViewModel.vacancyBannerOpened.collectAsState()
    val shouldShowVacancyBanner = remoteConfig.vacancyNotificationBannerEnabled && vacancyBannerOpened

    var timetableHeight by remember { mutableStateOf(0) }
    var topBarHeight by remember { mutableStateOf(0) }
    var bannerHeight by remember { mutableStateOf(0) }

    Column(Modifier.background(SNUTTColors.White900)) {
        TopBar(
            // top bar 높이 측정
            modifier = Modifier.onGloballyPositioned {
                topBarHeight = it.size.height
            },
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
                                tableListViewModel::changeTableName
                            )
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(
                        R.string.timetable_credit,
                        getCreditSumFromLectureList(table.lectureList)
                    ),
                    style = SNUTTTypography.body2,
                    maxLines = 1,
                    color = SNUTTColors.Gray200
                )
            },
            navigationIcon = {
                IconWithAlertDot(newSemesterNotify) { centerAlignedModifier ->
                    DrawerIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks { scope.launch { drawerState.open() } },
                    )
                }
            },
            actions = {
                TrashIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            val file = File(context.applicationContext.cacheDir, "android.jsbundle")
                            if (file.canRead()) {
                                file.delete()
                                context.toast("지워버리기~")
                            }
                        },
                )
                LectureListIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            val intent = Intent((context as Activity), RNModuleActivity::class.java)
                            intent.putExtra("message_from_native", table.title)
                            context.startActivity(intent)
                        },
                )
                LectureListIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.LecturesOfTable) },
                )
                ShareIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            shareScreenshotFromView(
                                view,
                                context,
                                topBarHeight,
                                if (shouldShowVacancyBanner) bannerHeight else 0,
                                timetableHeight
                            )
                        },
                )
                IconWithAlertDot(firstBookmarkAlert) { centerAlignedModifier ->
                    BookmarkPageIcon(
                        modifier = centerAlignedModifier
                            .size(30.dp)
                            .clicks {
                                navController.navigate(NavigationDestination.Bookmark) {
                                    launchSingleTop = true
                                }
                            },
                    )
                }
            }
        )
        if (shouldShowVacancyBanner) {
            VacancyBanner(
                onClick = {
                    navController.navigate(NavigationDestination.VacancyNotification)
                },
                onClose = {
                    scope.launch {
                        vacancyViewModel.closeVacancyBanner()
                    }
                },
                modifier = Modifier
                    .onGloballyPositioned { bannerHeight = it.size.height }
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .onGloballyPositioned { timetableHeight = it.size.height } // timetable 높이 측정
        ) {
            TimeTable(selectedLecture = null)
        }
    }
}

@Composable
fun VacancyBanner(
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(SNUTTColors.BannerBlue)
            .clicks { onClick() }
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RingingAlarmIcon(
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Text(
                text = stringResource(R.string.vacancy_banner_text),
                style = SNUTTTypography.body2.copy(
                    color = SNUTTColors.AllWhite
                )
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "NEW",
                style = SNUTTTypography.h5.copy(
                    fontSize = 8.sp,
                    color = SNUTTColors.AllWhite
                )
            )
        }
        Spacer(
            modifier = Modifier.weight(1f)
        )
        TipCloseIcon(
            modifier = Modifier
                .size(11.dp)
                .clicks { onClose() },
            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TimetablePagePreview() {
    TimetablePage()
}
