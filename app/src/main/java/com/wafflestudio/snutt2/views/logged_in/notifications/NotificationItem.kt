package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.components.compose.CalendarIcon
import com.wafflestudio.snutt2.components.compose.MegaphoneIcon
import com.wafflestudio.snutt2.components.compose.NotificationFriendIcon
import com.wafflestudio.snutt2.components.compose.NotificationTrashIcon
import com.wafflestudio.snutt2.components.compose.NotificationVacancyIcon
import com.wafflestudio.snutt2.components.compose.RefreshTimeIcon
import com.wafflestudio.snutt2.components.compose.WarningIcon
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.data.notifications.PreviewData
import com.wafflestudio.snutt2.deeplink.DeeplinkExecutor
import com.wafflestudio.snutt2.lib.data.SNUTTStringUtils
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun NotificationItem(notification: NotificationDto) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clicks { DeeplinkExecutor.execute(notification.deeplink) },
        border = null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            when (notification.type) {
                0 -> WarningIcon(Modifier.size(30.dp, 30.dp))
                1 -> NotificationFriendIcon(Modifier.size(30.dp, 30.dp))
                2 -> RefreshTimeIcon(Modifier.size(30.dp, 30.dp))
                3 -> CalendarIcon(Modifier.size(30.dp, 30.dp))
                4 -> NotificationVacancyIcon(Modifier.size(30.dp, 30.dp))
                5 -> NotificationTrashIcon(Modifier.size(30.dp, 30.dp))
                else -> MegaphoneIcon(Modifier.size(30.dp, 30.dp))
            }
            Column(modifier = Modifier) {
                Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
                    Text(text = notification.title, Modifier, fontSize = 14.sp)
                    Text(text = SNUTTStringUtils.getNotificationTimeFromDate(context = LocalContext.current, SNUTTStringUtils.getDateFromString(notification.createdAt)), modifier = Modifier.fillMaxWidth(), color = SNUTTColors.Gray30, textAlign = TextAlign.End)
                }
                Spacer(Modifier.padding(6.dp))
                Text(text = notification.message)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    NotificationItem(PreviewData.sampleNotifications[0])
}
