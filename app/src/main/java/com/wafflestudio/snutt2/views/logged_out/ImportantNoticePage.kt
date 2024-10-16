package com.wafflestudio.snutt2.views.logged_out

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.network.dto.core.RemoteConfigDto
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.LocalNavController
import com.wafflestudio.snutt2.views.LocalRemoteConfig
import com.wafflestudio.snutt2.views.NavigationDestination

@Composable
fun ImportantNoticePage() {
    val navController = LocalNavController.current
    val remoteConfig = LocalRemoteConfig.current
    val noticeConfig by remoteConfig.noticeConfig.collectAsState(RemoteConfigDto.NoticeConfig())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp, horizontal = 48.dp)
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_cat_retry),
            contentDescription = stringResource(R.string.sign_in_logo_title),
            modifier = Modifier.width(55.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = noticeConfig.title ?: "",
            style = SNUTTTypography.h3.copy(
                fontSize = 17.sp,
            ),
            color = SNUTTColors.Black900,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = noticeConfig.content ?: "",
            style = SNUTTTypography.body1,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            modifier = Modifier
                .size(121.dp, 33.dp)
                .clicks {
                    navController.navigate(NavigationDestination.AppReport)
                },
            shape = RoundedCornerShape(18.dp),
            color = SNUTTColors.SNUTTVacancy,
        ) {
            Box(
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(R.string.send_report),
                    style = SNUTTTypography.h4.copy(
                        color = SNUTTColors.AllWhite,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}
