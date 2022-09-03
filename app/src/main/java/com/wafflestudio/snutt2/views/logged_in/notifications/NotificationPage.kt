package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.AlarmOnIcon
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun NotificationPage() {
    val navController = LocalNavController.current
    val vm = hiltViewModel<NotificationsViewModel>()

    val notificationList = vm.notificationList.collectAsLazyPagingItems()
    val refreshState = notificationList.loadState.refresh
    val appendState = notificationList.loadState.append

    Column {
        SimpleTopBar(title = stringResource(R.string.notifications_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() })

        when {
            refreshState is LoadState.NotLoading && appendState.endOfPaginationReached && notificationList.itemCount < 1 -> NotificationPlaceholder()
            refreshState is LoadState.Error -> NotificationError()
            else -> LazyColumn {
                items(notificationList.itemCount) { index ->
                    NotificationItem(notificationList[index])
                }
            }
        }
    }
}

@Composable
fun NotificationItem(info: NotificationDto?) {
    Row(modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp)) {
        when (info?.type) {
            0 -> painterResource(R.drawable.ic_warning)
            1 -> painterResource(R.drawable.ic_calendar)
            2 -> painterResource(R.drawable.ic_refresh)
            3 -> painterResource(R.drawable.ic_trash)
            else -> null
        }?.let {
            Image(painter = it, contentDescription = "Message", modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row(modifier = Modifier.fillMaxWidth()) {
                when (info?.type) {
                    0 -> stringResource(id = R.string.notifications_noti_warning)
                    1 -> stringResource(id = R.string.notifications_noti_add)
                    2 -> stringResource(id = R.string.notifications_noti_update)
                    3 -> stringResource(id = R.string.notifications_noti_delete)
                    else -> null
                }?.let {
                    Text(text = it, style = SNUTTTypography.h4)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = if (info != null) getNotificationTime(info) else "-",
                    style = SNUTTTypography.body2.copy(color = SNUTTColors.Gray600),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(
                text = info?.message ?: "",
                style = SNUTTTypography.body2,
            )
        }
    }
}

@Composable
fun NotificationError() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        WarningIcon(
            modifier = Modifier.size(40.dp), colorFilter = ColorFilter.tint(SNUTTColors.Gray400)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.common_network_failure),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray400)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlarmOnIcon(
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.notifications_placeholder_title),
            style = SNUTTTypography.h2.copy(color = SNUTTColors.Gray300),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(R.string.notifications_placeholder_description),
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Gray300),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationPagePreview() {
    NotificationPage()
}
