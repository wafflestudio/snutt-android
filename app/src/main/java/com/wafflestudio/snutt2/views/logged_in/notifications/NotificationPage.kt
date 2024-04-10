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
import androidx.compose.runtime.collectAsState
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
    LazyColumn(
        userScrollEnabled = true,
        modifier = Modifier
    ) {
        items(notificationList){
            NotificationItem(info = it!!)
        }
    }
}

@Composable
fun NotificationItem(info: NotificationDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)
    ){
        when(info.type){
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
            ){
                Text(text = info.title)
                Text(text = info.createdAt)
            }
            Text(text = info.message)
        }
    }
}

@Composable
fun NotificationError() {
    // TODO: 네트워크 에러 시 보여줄 페이지
}

@Composable
fun NotificationPlaceholder() {
    // TODO: 알림이 하나도 없을 때 보여줄 페이지 (힌트: 새로 가입한 계정이면 이 화면을 볼 수 있다)
}
