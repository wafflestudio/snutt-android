package com.wafflestudio.snutt2.views.logged_in.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.LocalOverScrollConfiguration
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.ui.SnuttTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import kotlinx.coroutines.flow.Flow

@ExperimentalFoundationApi
@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {

    private val vm: NotificationsViewModel by activityViewModels()
    private val typeList: List<String> = listOf("", "추가", "업데이트", "삭제", "경고") // 임시
    private val spoqaHanSans = FontFamily(
        Font(R.font.spoqa_han_sans_regular, FontWeight.Medium),
        Font(R.font.spoqa_han_sans_bold, FontWeight.Bold),
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnuttTheme {
                    Column {
                        TopBar()
                        NotificationList(notifications = vm.notifications)
                    }
                }
            }
        }
    }

    @Composable
    fun NotificationItem(info: NotificationDto?) {
        val subHeadingFontStyle = TextStyle(fontSize = 14.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Bold, color = colorResource(R.color.black))
        val detailFontStyle = TextStyle(fontSize = 12.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Medium, color = colorResource(R.color.black))

        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row {
                Image(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = "Message",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = typeList[info?.type ?: 0], style = subHeadingFontStyle)
                        Text(text = info?.createdAt?.substring(0, 10) ?: "", style = detailFontStyle, color = colorResource(R.color.created_at))
                    }
                    Spacer(modifier = Modifier.height(7.dp))
                    Text(text = info?.message ?: "", style = detailFontStyle)
                }
            }
        }
    }

    @Composable
    fun NotificationList(notifications: Flow<PagingData<NotificationDto>>) {
        val items: LazyPagingItems<NotificationDto> = notifications.collectAsLazyPagingItems()
        CompositionLocalProvider(LocalOverScrollConfiguration provides null) {
            LazyColumn {
                items(items) { notification ->
                    for (i: Int in 1..10) NotificationItem(notification)
                }
            }
        }
    }

    @Composable
    fun TopBar() {
        val titleStyle = TextStyle(fontSize = 17.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Bold, color = colorResource(R.color.black))

        Surface(elevation = 2.dp) {
            Row(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "back button",
                    modifier = Modifier
                        .size(30.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = { findNavController().popBackStack() },
                            indication = null
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = "알림", style = titleStyle)
            }
        }
    }
}
