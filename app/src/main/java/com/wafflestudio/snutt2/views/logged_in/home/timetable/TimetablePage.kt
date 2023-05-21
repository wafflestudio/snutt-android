package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.BookmarkPageIcon
import com.wafflestudio.snutt2.components.compose.ComposableStatesWithScope
import com.wafflestudio.snutt2.components.compose.DrawerIcon
import com.wafflestudio.snutt2.components.compose.IconWithAlertDot
import com.wafflestudio.snutt2.components.compose.LectureListIcon
import com.wafflestudio.snutt2.components.compose.ShareIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getCreditSumFromLectureList
import com.wafflestudio.snutt2.lib.shareScreenshotFromView
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalBottomSheetState
import com.wafflestudio.snutt2.views.LocalDrawerState
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalTableState
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.settings.UserViewModel
import com.wafflestudio.snutt2.views.logged_in.home.share.ShareTableViewModel
import com.wafflestudio.snutt2.views.logged_in.home.share.SharedBottomSheet
import com.wafflestudio.snutt2.views.logged_in.home.share.shareLink
import com.wafflestudio.snutt2.views.logged_in.home.showTitleChangeDialog
import kotlinx.coroutines.launch

@Composable
fun TimetablePage() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val view = LocalView.current
    val navController = LocalNavController.current
    val drawerState = LocalDrawerState.current
    val table = LocalTableState.current.table
    val bottomSheet = LocalBottomSheetState.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val composableStates = ComposableStatesWithScope(scope)
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val sharedTableViewModel: ShareTableViewModel = hiltViewModel()
    val userViewModel = hiltViewModel<UserViewModel>()
    val newSemesterNotify by tableListViewModel.newSemesterNotify.collectAsState(false)
    val firstBookmarkAlert by userViewModel.firstBookmarkAlert.collectAsState()

    var timetableHeight by remember { mutableStateOf(0) }
    var topBarHeight by remember { mutableStateOf(0) }

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
                    modifier = Modifier.clicks {
                        showTitleChangeDialog(table.title, table.id, composableStates, tableListViewModel::changeTableName)
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
                LectureListIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks { navController.navigate(NavigationDestination.LecturesOfTable) },
                )
                ShareIcon(
                    modifier = Modifier
                        .size(30.dp)
                        .clicks {
                            bottomSheet.setSheetContent {
                                SharedBottomSheet(
                                    onShareLink = {
                                        scope.launch {
                                            launchSuspendApi(apiOnProgress, apiOnError) {
                                                val link = sharedTableViewModel.createShareLink(table.id)
                                                shareLink(context, link)
                                            }
                                        }
                                    },
                                    onShareImage = {
                                        shareScreenshotFromView(
                                            view,
                                            context,
                                            topBarHeight,
                                            timetableHeight
                                        )
                                    }
                                )
                            }
                            scope.launch {
                                bottomSheet.show()
                            }
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

@Preview(showBackground = true)
@Composable
fun TimetablePagePreview() {
    TimetablePage()
}
