package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.NavigationDestination

@Composable
fun ImportantNoticePage() {
    val navController = LocalNavController.current

    val importantNoticeViewModel = hiltViewModel<ImportantNoticeViewModel>()
    val importantNotice = importantNoticeViewModel.importantNotice.collectAsState()

    LaunchedEffect(Unit) {
        importantNoticeViewModel.getConfigs()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cat_retry),
            contentDescription = stringResource(R.string.sign_in_logo_title),
            modifier = Modifier.width(48.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = importantNotice.value.title ?: "",
            style = SNUTTTypography.h3.copy(
                fontSize = 17.sp,
            ),
            color = SNUTTColors.Black900,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = importantNotice.value.content ?: "",
            style = SNUTTTypography.body1,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable {
                navController.navigate(NavigationDestination.AppReport)
            },
        ) {
            Text(
                text = "문의사항은 아래 메일로 부탁드립니다.",
                style = SNUTTTypography.body1,
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "snutt@wafflestudio.com",
                style = SNUTTTypography.body1,
            )
        }
    }
}
