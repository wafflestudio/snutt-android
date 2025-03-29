package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto

// TODO
@Composable
fun NotificationScreen(viewModel: NotificationsViewModel = hiltViewModel(),
                       onNavigateBack: () -> Unit){
    val notifications: LazyPagingItems<NotificationDto> =
        viewModel.notifications.collectAsLazyPagingItems()

    Column(modifier = Modifier.fillMaxSize()) {
        SimpleTopBar(modifier = Modifier, stringResource(R.string.notifications_app_bar_title),
            onNavigateBack)

        LazyColumn {
            items(notifications.itemCount) { index ->
                val notification = notifications[index]
                if (notification != null) {
                    NotificationItem(notification)
                }
            }

            notifications.apply {
                when {
                    loadState.refresh is androidx.paging.LoadState.Loading -> {
                        item { CircularProgressIndicator() }
                    }

                    loadState.append is androidx.paging.LoadState.Loading -> {
                        item { CircularProgressIndicator() }
                    }

                    loadState.append is androidx.paging.LoadState.Error -> {
                        item { Text("Error loading notifications") }
                    }
                }
            }
        }
    }
}
