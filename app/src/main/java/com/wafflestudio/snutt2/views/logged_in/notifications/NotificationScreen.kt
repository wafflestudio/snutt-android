package com.wafflestudio.snutt2.views.logged_in.notifications

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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AlarmOnIcon
import com.wafflestudio.snutt2.components.compose.CalendarIcon
import com.wafflestudio.snutt2.components.compose.ChevronIcon
import com.wafflestudio.snutt2.components.compose.MegaphoneIcon
import com.wafflestudio.snutt2.components.compose.NotificationFriendIcon
import com.wafflestudio.snutt2.components.compose.NotificationTrashIcon
import com.wafflestudio.snutt2.components.compose.NotificationVacancyIcon
import com.wafflestudio.snutt2.components.compose.RefreshTimeIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.deeplink.DeeplinkExecutor
import com.wafflestudio.snutt2.domainmodel.Notification
import com.wafflestudio.snutt2.domainmodel.NotificationType
import com.wafflestudio.snutt2.domainmodel.PreviewData
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import kotlinx.coroutines.flow.flowOf

@Composable
fun NotificationRoute(
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = hiltViewModel(),
) {
    val navController = LocalNavController.current
    val notificationList = viewModel.notificationList.collectAsLazyPagingItems()
    val notificationUiState = notificationList.notificationUiState()

    NotificationPage(
        onBackClick = {
            if (navController.currentDestination?.route == NavigationDestination.Notification) {
                navController.popBackStack()
            }
        },
        uiState = notificationUiState,
        modifier = modifier,
    )
}

@Composable
fun NotificationPage(
    modifier: Modifier = Modifier,
    uiState: NotificationUiState,
    onBackClick: () -> Unit,
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
                items(uiState.notificationList) { notification ->
                    notification?.let {
                        NotificationItem(notification) {
                            DeeplinkExecutor.execute(it.deeplink)
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
                        style = SNUTTTypography.h4.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold),
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = getNotificationTime(context, notification.createdAt),
                        style = SNUTTTypography.body1.copy(fontSize = 13.sp, color = SNUTTColors.Gray2),
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
                        ChevronIcon(modifier = Modifier.size(20.dp))
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
        NotificationType.Warning -> WarningIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Calendar -> CalendarIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.RefreshTime -> RefreshTimeIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Trash -> NotificationTrashIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Vacancy -> NotificationVacancyIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Friend -> NotificationFriendIcon(
            modifier = Modifier.size(30.dp),
            colorFilter = ColorFilter.tint(if (isDarkMode()) SNUTTColors.Gray10 else SNUTTColors.Black900),
        )

        NotificationType.Megaphone -> MegaphoneIcon(
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
fun NotificationPagePreview() {
    val data =
        PagingData.from(PreviewData.sampleNotifications)
    val flow = flowOf(data)
    val a = flow.collectAsLazyPagingItems()
    NotificationPage(uiState = NotificationUiState.Success(a), onBackClick = {})
}
