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
import timber.log.Timber
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@ExperimentalFoundationApi
@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {

    private val vm: NotificationsViewModel by activityViewModels()
    private val spoqaHanSans = FontFamily(
        Font(R.font.spoqa_han_sans_regular, FontWeight.Medium),
        Font(R.font.spoqa_han_sans_bold, FontWeight.Bold),
    )
    private val subHeadingFontStyle = TextStyle(fontSize = 14.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Bold)
    private val detailFontStyle = TextStyle(fontSize = 12.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Medium)
    private val titleStyle = TextStyle(fontSize = 17.sp, fontFamily = spoqaHanSans, fontWeight = FontWeight.Bold)

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

        Column(modifier = Modifier.padding(all = 16.dp)) {
            Row {
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
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        when (info?.type) {
                            0 -> context?.getString(R.string.notifications_noti_warning)
                            1 -> context?.getString(R.string.notifications_noti_add)
                            2 -> context?.getString(R.string.notifications_noti_update)
                            3 -> context?.getString(R.string.notifications_noti_delete)
                            else -> null
                        }?.let {
                            Text(text = it, style = subHeadingFontStyle)
                        }
                        Text(
                            text = try {
                                val format: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                                format.timeZone = TimeZone.getTimeZone("UTC")
                                val date1 = format.parse(info?.createdAt) ?: Date()
                                val date2 = Date()

                                val diff = date2.time - date1.time
                                val hours = diff / (1000 * 60 * 60)
                                val days = hours / 24
                                when {
                                    days > 0 -> {
                                        DateFormat.getDateInstance().format(date1)
                                    }
                                    hours > 0 -> {
                                        "$hours 시간 전"
                                    }
                                    else -> {
                                        "방금"
                                    }
                                }
                            } catch (e: ParseException) {
                                Timber.e("notification created time parse error!")
                                "-"
                            },
                            style = detailFontStyle, color = colorResource(R.color.created_at)
                        )
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
                    NotificationItem(notification)
                }
            }
        }
    }

    @Composable
    fun TopBar() {
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
                            interactionSource = MutableInteractionSource(),
                            onClick = { findNavController().popBackStack() },
                            indication = null
                        )
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = getString(R.string.notifications_app_bar_title), style = titleStyle)
            }
        }
    }
}
