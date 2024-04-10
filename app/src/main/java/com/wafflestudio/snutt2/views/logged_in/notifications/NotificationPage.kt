package com.wafflestudio.snutt2.views.logged_in.notifications

import android.util.Log
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
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils.getNotificationTime
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination

@Composable
fun NotificationPage(
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
) {
    val notificationList = notificationsViewModel.notificationList.collectAsLazyPagingItems()
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .height(30.dp)
                .fillMaxWidth()
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painter = painterResource(id = R.drawable.ic_arrow_back), "")
            }
            Text(text = "알림")
        }

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )

        if (notificationList.loadState.refresh is LoadState.Error) {
            NotificationError()
        }
        else if (notificationList.itemCount==0) {
            NotificationPlaceholder()
        }
        else {
            LazyColumn(
                userScrollEnabled = true,
                modifier = Modifier
            ) {
                items(notificationList) {
                    NotificationItem(info = it!!)
                }
            }
        }
    }
}

@Composable
fun NotificationItem(info: NotificationDto) {
    val context = LocalContext.current

    Column {
        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
        ) {
            when (info.type) {
                0 -> WarningIcon(
                    modifier = Modifier.size(30.dp)
                )

                1 -> CalendarIcon(
                    modifier = Modifier.size(30.dp)
                )

                2 -> RefreshTimeIcon(
                    modifier = Modifier.size(30.dp)
                )

                3 -> NotificationTrashIcon(
                    modifier = Modifier.size(30.dp)
                )

                4 -> NotificationVacancyIcon(
                    modifier = Modifier.size(30.dp)
                )

                5 -> NotificationFriendIcon(
                    modifier = Modifier.size(30.dp)
                )

                else -> MegaphoneIcon(
                    modifier = Modifier.size(30.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = info.title)
                    Text(text = getNotificationTime(context = context, info))
                }
                Text(text = info.message)
            }
        }

        Spacer(modifier = Modifier.height(5.dp))

        Divider(
            modifier = Modifier
                .height(1.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
fun NotificationError() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Icon(painter = painterResource(id = R.drawable.ic_warning), "")

        Text("네트워크 연결 상태를 확인해주세요.")
    }
}

@Composable
fun NotificationPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 25.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Icon(painter = painterResource(id = R.drawable.ic_alarm_default), "")

        Text("알림이 없습니다")

        Text("넣은 강좌의 수강편람이 바뀌거나, 새로운 수강편람이 뜨면 알림을 줍니다")
    }
}
