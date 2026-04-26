package com.wafflestudio.snutt2.feature.notifications

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.Notification
import com.wafflestudio.snutt2.domain.model.NotificationType
import com.wafflestudio.snutt2.ui.preview.PreviewData
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.preview.rememberFakeLazyPagingItems
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.util.getNotificationTime

@Composable
fun NotificationRoute(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToTimetableLectureDetail: (lectureId: String, timetableId: String) -> Unit,
    onNavigateToBookmarkLectureDetail: (lectureId: String, year: Long, semester: Long) -> Unit,
    onNavigateToFriends: () -> Unit,
) {
    val notificationList = viewModel.notificationList.collectAsLazyPagingItems()
    val notificationUiState = notificationList.notificationUiState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is NotificationUiEvent.NavigateToTimetableLectureDetail ->
                    onNavigateToTimetableLectureDetail(event.lectureId, event.timetableId)

                is NotificationUiEvent.NavigateToBookmarkLectureDetail ->
                    onNavigateToBookmarkLectureDetail(event.lectureId, event.year, event.semester)

                is NotificationUiEvent.NavigateToFriends ->
                    onNavigateToFriends()
            }
        }
    }

    NotificationScreen(
        uiState = notificationUiState,
        modifier = modifier,
        onBackClick = onNavigateBack,
        onNotificationClick = viewModel::onNotificationClick,
    )
}

@Composable
fun NotificationScreen(
    modifier: Modifier = Modifier,
    uiState: NotificationUiState,
    onBackClick: () -> Unit,
    onNotificationClick: (Notification) -> Unit,
) {
    Column(
        modifier = modifier
            .background(SNUTTColors.White900)
            .fillMaxSize(),
    ) {
        SimpleTopBar(
            title = stringResource(R.string.notifications_app_bar_title),
            onClickNavigateBack = onBackClick,
        )

        when (uiState) {
            NotificationUiState.Empty -> NotificationPlaceholder()
            NotificationUiState.Error -> NotificationError()
            NotificationUiState.Loading -> {}
            is NotificationUiState.Success -> LazyColumn {
                items(
                    count = uiState.notificationList.itemCount,
                ) { index ->
                    uiState.notificationList[index]?.let { notification ->
                        NotificationItem(notification) {
                            onNotificationClick(notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification, onClick: () -> Unit) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .clicks {
                onClick()
            }
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            NotificationIcon(notification.type)
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.padding(top = 4.dp, bottom = 7.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = notification.title,
                        style = SNUTTTypography.h4.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = getNotificationTime(context, notification.createdAt),
                        style = SNUTTTypography.body1.copy(
                            fontSize = 13.sp,
                            color = SNUTTColors.Gray2,
                        ),
                        maxLines = 1,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier.weight(1f),
                        text = notification.message,
                        style = SNUTTTypography.body1.copy(fontSize = 13.sp, lineHeight = 18.2.sp),
                    )
                    if (!notification.deeplink.isNullOrEmpty()) {
                        Spacer(modifier = Modifier.width(16.dp))
                        SnuttIcon(
                            R.drawable.ic_arrow_right,
                            modifier = Modifier.size(20.dp),
                            colorFilter = null,
                            contentDescription = "add arrow",
                        )
                    }
                }
            }
        }
        Divider(color = SNUTTColors.Black250, thickness = 0.5.dp)
    }
}

@Composable
fun NotificationIcon(type: NotificationType) {
    when (type) {
        NotificationType.Warning -> SnuttIcon(
            R.drawable.ic_warning,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Calendar -> SnuttIcon(
            R.drawable.ic_calendar,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.RefreshTime -> SnuttIcon(
            R.drawable.ic_refresh_time,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Trash -> SnuttIcon(
            R.drawable.ic_trash_new,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Vacancy -> SnuttIcon(
            R.drawable.ic_ringing_alarm_notification,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Friend -> SnuttIcon(
            R.drawable.ic_ringing_alarm_notification,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Megaphone -> SnuttIcon(
            R.drawable.ic_megaphone,
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )
    }
}

@Composable
fun NotificationError() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SnuttIcon(
            R.drawable.ic_warning,
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(SNUTTColors.Gray200),
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
        SnuttIcon(
            R.drawable.tab_alarm_on,
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

@SnuttPreview
@Composable
private fun NotificationScreen_List() {
    val pagingItems = rememberFakeLazyPagingItems(PreviewData.sampleNotifications)
    SnuttPreviewSurface {
        NotificationScreen(
            uiState = NotificationUiState.Success(pagingItems),
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun NotificationScreen_Empty() {
    SnuttPreviewSurface {
        NotificationScreen(
            uiState = NotificationUiState.Empty,
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun NotificationScreen_Error() {
    SnuttPreviewSurface {
        NotificationScreen(
            uiState = NotificationUiState.Error,
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun NotificationItem_WithDeeplink() {
    SnuttPreviewSurface {
        NotificationItem(
            notification = PreviewData.sampleNotifications.first(),
            onClick = {},
        )
    }
}

@SnuttPreview
@Composable
private fun NotificationItem_NoDeeplink() {
    SnuttPreviewSurface {
        NotificationItem(
            notification = PreviewData.sampleNotifications.first().copy(deeplink = null),
            onClick = {},
        )
    }
}
