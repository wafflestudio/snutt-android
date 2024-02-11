package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination

@Composable
fun NotificationPage() {
    val navController = LocalNavController.current
    val vm = hiltViewModel<NotificationsViewModel>()

    val notificationList = vm.notificationList.collectAsLazyPagingItems()
    val refreshState = notificationList.loadState.refresh
    val appendState = notificationList.loadState.append

    Column(modifier = Modifier.background(SNUTTColors.White900)) {
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
                    it?.let { NotificationItem(it) }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(info: NotificationDto) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 8.dp, end = 2.dp),
        ) {
            when (info.type) {
                0 -> WarningIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                1 -> CalendarIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                2 -> RefreshTimeIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                3 -> NotificationTrashIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                4 -> NotificationVacancyIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                5 -> NotificationFriendIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )

                else -> MegaphoneIcon(
                    modifier = Modifier.size(30.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900),
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier.padding(top = 4.dp, bottom = 7.dp),
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = info.title, style = SNUTTTypography.h4.copy(fontWeight = FontWeight.SemiBold))
                    Spacer(modifier = Modifier.weight(1f))
                    Text(
                        text = getNotificationTime(context, info),
                        style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray2),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = info.message,
                    style = SNUTTTypography.body1,
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
    NotificationItem(NotificationDto("asdf", "title", "message", "2024-01-17T12:04:59.998Z", 0, null))
}

@Preview(showBackground = true)
@Composable
fun NotificationPagePreview() {
    NotificationPage()
}
