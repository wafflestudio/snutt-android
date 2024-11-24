package com.wafflestudio.snutt2.notification

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
import com.wafflestudio.snutt2.components.compose.MegaphoneIcon
import com.wafflestudio.snutt2.components.compose.NotificationFriendIcon
import com.wafflestudio.snutt2.components.compose.NotificationTrashIcon
import com.wafflestudio.snutt2.components.compose.NotificationVacancyIcon
import com.wafflestudio.snutt2.components.compose.RefreshTimeIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.deeplink.DeeplinkExecutor
import com.wafflestudio.snutt2.domain_model.Notification
import com.wafflestudio.snutt2.domain_model.NotificationType
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination
import com.wafflestudio.snutt2.views.logged_in.notifications.NotificationsViewModel
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
                .padding(top = 8.dp, bottom = 8.dp, end = 2.dp),
        ) {
            NotificationIcon(notification.type)
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.padding(top = 4.dp, bottom = 7.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = notification.title,
                        style = SNUTTTypography.h4.copy(
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = getNotificationTime(context, notification),
                        style = SNUTTTypography.body1.copy(
                            fontSize = 13.sp,
                            color = SNUTTColors.Gray2,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = notification.message,
                    style = SNUTTTypography.body1.copy(fontSize = 13.sp, lineHeight = 18.2.sp),
                )
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
        PagingData.from(listOf(Notification("테스트 알림", "테스트 문구", "", NotificationType.Trash, "")))
    val flow = flowOf(data)
    val a = flow.collectAsLazyPagingItems()
    NotificationPage(uiState = NotificationUiState.Success(a), onBackClick = {})
}
