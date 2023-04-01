package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.paging.compose.items
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.*
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalApiOnError
import com.wafflestudio.snutt2.views.LocalApiOnProgress
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.launchSuspendApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@Composable
fun NotificationPage() {
    val navController = LocalNavController.current
    val viewModel = hiltViewModel<NotificationsViewModel>()
//    var loaded by remember { mutableStateOf(false) }

//    lateinit var notificationFlow: Flow<PagingData<NotificationDto>>


//    LaunchedEffect(loaded) {
//        if(!loaded) {
//            notificationFlow = viewModel.getNotificationStream()
//            loaded = true
//        }
//    }

    val notifications = viewModel.getNotificationStream().collectAsLazyPagingItems()

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900)
    ) {
        SimpleTopBar(
            title = stringResource(R.string.notifications_app_bar_title),
            onClickNavigateBack = { navController.popBackStack() }
        )
//        if(loaded) {
//            NotificationList(
//                notifications = notificationFlow.collectAsLazyPagingItems()
//            )
//        }
//        else {
//            CircularProgressIndicator()
//        }
        NotificationList(
            notifications = notifications
        )
    }
}

@Composable
fun NotificationList(
    notifications: LazyPagingItems<NotificationDto>
) {
    if(notifications.itemCount == 0) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NotificationIcon(Modifier.size(40.dp))
            Spacer(Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.notifications_placeholder_title),
                style = SNUTTTypography.h2
            )
            Spacer(Modifier.size(20.dp))
            Text(
                modifier = Modifier.padding(horizontal = 40.dp),
                text = stringResource(R.string.notifications_placeholder_description),
                style = SNUTTTypography.body1,
                textAlign = TextAlign.Center
            )
        }
    }
    else {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(notifications) { notificationDto ->
                notificationDto?.let {
                    NotificationItem(
                        notification = notificationDto
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationDto
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        when(notification.type) {
            0 -> WarningIcon(Modifier.size(20.dp))
            1 -> CalendarIcon(Modifier.size(20.dp))
            2 -> RefreshIcon(Modifier.size(20.dp))
            3 -> TrashIcon(Modifier.size(20.dp))
            4 -> WarningIcon(Modifier.size(20.dp))
        }
        Spacer(Modifier.size(10.dp))
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text =
                        when(notification.type) {
                            0 -> stringResource(R.string.notifications_noti_warning)
                            1 -> stringResource(R.string.notifications_noti_add)
                            2 -> stringResource(R.string.notifications_noti_update)
                            3 -> stringResource(R.string.notifications_noti_delete)
                            4 -> stringResource(R.string.notifications_noti_change)
                            else -> ""
                        },
                    style = SNUTTTypography.h4
                )
                Text(
                    text = notification.createdAt,
                    style = SNUTTTypography.body2,
                )
            }
            Text(
                text = notification.message,
                style = SNUTTTypography.body2,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun NotificationItemPreview() {
    NotificationItem(
        NotificationDto(
            id = null,
            message = "2023-1학기 '화성학' 시간표의 '통계학' 강의가 업데이트 되었습니다.(항목:기타)",
            createdAt = "2023.04.01.",
            type = 2,
            detail = null
        )
    )
}
