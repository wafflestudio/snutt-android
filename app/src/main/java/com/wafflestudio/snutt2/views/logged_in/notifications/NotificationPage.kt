package com.wafflestudio.snutt2.views.logged_in.notifications

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AlarmOnIcon
import com.wafflestudio.snutt2.components.compose.CalendarIcon
import com.wafflestudio.snutt2.components.compose.MegaphoneIcon
import com.wafflestudio.snutt2.components.compose.NotificationFriendIcon
import com.wafflestudio.snutt2.components.compose.NotificationTrashIcon
import com.wafflestudio.snutt2.components.compose.NotificationVacancyIcon
import com.wafflestudio.snutt2.components.compose.RefreshTimeIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.launchSuspendApi
import com.wafflestudio.snutt2.views.logged_in.home.TableListViewModel
import com.wafflestudio.snutt2.views.logged_in.home.search.SearchViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.LectureDetailViewModel
import com.wafflestudio.snutt2.views.logged_in.lecture_detail.ModeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun NotificationPage() {
    val navController = LocalNavController.current
    val vm = hiltViewModel<NotificationsViewModel>()
    val lectureDetailViewModel = hiltViewModel<LectureDetailViewModel>()
    val tableListViewModel = hiltViewModel<TableListViewModel>()
    val searchViewModel = hiltViewModel<SearchViewModel>()
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val scope = rememberCoroutineScope()

    val notificationList = vm.notificationList.collectAsLazyPagingItems()
    val refreshState = notificationList.loadState.refresh
    val appendState = notificationList.loadState.append

    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.notifications_app_bar_title),
            onClickNavigateBack = {
                if (navController.currentDestination?.route == NavigationDestination.Notification) {
                    navController.popBackStack()
                }
            },
        )

        when {
            refreshState is LoadState.NotLoading && appendState.endOfPaginationReached && notificationList.itemCount < 1 -> NotificationPlaceholder()
            refreshState is LoadState.Error -> NotificationError()
            else -> LazyColumn {
                items(notificationList) {
                    it?.let {
                        NotificationItem(
                            it,
                            onClick = { deeplink ->
                                val uri = Uri.parse(deeplink)

                                when (uri.host) {
                                    NavigationDestination.TimetableLecture -> {
                                        val timetableId = uri.getQueryParameter("timetableId") ?: return@NotificationItem
                                        val lectureId = uri.getQueryParameter("lectureId") ?: return@NotificationItem

                                        scope.launch(Dispatchers.IO) {
                                            launchSuspendApi(apiOnProgress, apiOnError, loadingIndicatorTitle = "잠시만 기다려 주세요...") {
                                                tableListViewModel.searchTableById(timetableId).lectureList.find {
                                                    it.lecture_id == lectureId
                                                }?.let {
                                                    lectureDetailViewModel.initializeEditingLectureDetail(it, ModeType.Viewing)
                                                    withContext(Dispatchers.Main) {
                                                        navController.navigate("${NavigationDestination.TimetableLecture}?tableId=$timetableId")
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    NavigationDestination.Bookmark -> {
                                        val year = uri.getQueryParameter("year")?.toLongOrNull() ?: return@NotificationItem
                                        val semester = uri.getQueryParameter("semester")?.let {
                                            when (it) {
                                                // FIXME
                                                "SPRING" -> 1L
                                                "SUMMER" -> 2L
                                                "FALL" -> 3L
                                                "WINTER" -> 4L
                                                else -> 0L
                                            }
                                        } ?: return@NotificationItem
                                        val lectureId = uri.getQueryParameter("lectureId") ?: return@NotificationItem

                                        scope.launch(Dispatchers.IO) {
                                            launchSuspendApi(apiOnProgress, apiOnError, loadingIndicatorTitle = "잠시만 기다려 주세요...") {
                                                searchViewModel.getBookmarkLecture(year, semester, lectureId)?.let {
                                                    lectureDetailViewModel.initializeEditingLectureDetail(it, ModeType.Viewing)
                                                    withContext(Dispatchers.Main) {
                                                        navController.navigate(NavigationDestination.TimetableLecture)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(info: NotificationDto, onClick: (String) -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .clicks {
                info.deeplink?.let(onClick)
            }
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 2.dp),
        ) {
            when (info.type) {
                0 -> WarningIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                1 -> CalendarIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                2 -> RefreshTimeIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                3 -> NotificationTrashIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                4 -> NotificationVacancyIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                5 -> NotificationFriendIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )

                else -> MegaphoneIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.padding(top = 4.dp, bottom = 7.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = info.title, style = SNUTTTypography.h4.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = getNotificationTime(context, info),
                        style = SNUTTTypography.body1.copy(fontSize = 13.sp, color = SNUTTColors.Gray2),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = info.message,
                    style = SNUTTTypography.body1.copy(fontSize = 13.sp, lineHeight = 18.2.sp),
                )
            }
        }
        Divider(color = SNUTTColors.Black250, thickness = 0.5.dp)
    }
}

@Composable
fun NotificationError() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        WarningIcon(
            modifier = Modifier.size(40.dp), colorFilter = ColorFilter.tint(SNUTTColors.Gray200),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.common_network_failure),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray200),
        )
    }
}

@Composable
fun NotificationPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 30.dp, end = 30.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AlarmOnIcon(
            modifier = Modifier.size(40.dp),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.notifications_placeholder_title),
            style = SNUTTTypography.h2.copy(color = SNUTTColors.Gray200),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.notifications_placeholder_description),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray200),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    NotificationItem(NotificationDto("asdf", "title", "message", "2024-01-17T12:04:59.998Z", 0, null, deeplink = ""), onClick = {})
}

@Preview(showBackground = true)
@Composable
fun NotificationPagePreview() {
    NotificationPage()
}
