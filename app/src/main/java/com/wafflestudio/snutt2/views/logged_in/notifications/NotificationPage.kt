package com.wafflestudio.snutt2.views.logged_in.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import com.wafflestudio.snutt2.components.compose.*
import androidx.compose.material.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.views.LocalNavController

@Composable
fun NotificationPage(

) {
    val viewModel = hiltViewModel<NotificationsViewModel>()
    val navController = LocalNavController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Top Bar ("< 알림")
        SimpleTopBar(
            title = stringResource(R.string.notifications_app_bar_title),
            onClickNavigateBack = {
                navController.popBackStack()
            }
        )

        // when notification exist
        // TODO...


        // When notification doesn't exist
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Notification Icon
            NotificationIcon()

            // Space
            Spacer(modifier = Modifier.height(10.dp))
            
            // 알림이 없습니다 
            Text(text = stringResource(id = R.string.notifications_placeholder_title))
            
            // Space
            Spacer(modifier = Modifier.height(10.dp))
            
            Text(text = stringResource(id = R.string.notifications_placeholder_description))

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Preview
@Composable
fun NotificationPagePreview() {
    NotificationPage()
}
