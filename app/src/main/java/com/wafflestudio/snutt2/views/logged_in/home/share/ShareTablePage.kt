package com.wafflestudio.snutt2.views.logged_in.home.share

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.LogoIcon
import com.wafflestudio.snutt2.components.compose.TopBar
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun ShareTablePage() {
    ShareTableTutorial()
}

@Composable
fun ShareTableTutorial() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SNUTTColors.White900),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TopBar(
            title = {
                Text(
                    text = stringResource(R.string.share_table_page_title),
                    style = SNUTTTypography.h3,
                )
            },
            navigationIcon = {
                LogoIcon(
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(SNUTTColors.Black900)
                )
            },
        )
        Spacer(modifier = Modifier.height(50.dp))
        Image(
            painter = painterResource(R.drawable.share_table_page_tutorial_image_1),
            contentDescription = ""
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(stringResource(R.string.share_table_page_tutorial_text_1), style = SNUTTTypography.h2)
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(R.string.share_table_page_tutorial_text_2), style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp))
        Text(stringResource(R.string.share_table_page_tutorial_text_3), style = SNUTTTypography.subtitle2.copy(fontSize = 12.sp))
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = stringResource(R.string.share_table_page_tutorial_text_4),
            style = SNUTTTypography.body2.copy(fontSize = 10.sp, color = SNUTTColors.PurpleBlue),
            modifier = Modifier.padding(start = 150.dp),
        )
        Column(modifier = Modifier.padding(start = 40.dp)) {
            Image(
                painter = painterResource(R.drawable.share_table_page_tutorial_image_2),
                contentDescription = "",
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_5),
                style = SNUTTTypography.body2.copy(fontSize = 10.sp, color = SNUTTColors.PurpleBlue),
            )
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_6),
                style = SNUTTTypography.body2.copy(fontSize = 10.sp, color = SNUTTColors.PurpleBlue),
            )
            Text(
                text = stringResource(R.string.share_table_page_tutorial_text_7),
                style = SNUTTTypography.body2.copy(fontSize = 10.sp, color = SNUTTColors.PurpleBlue),
            )
        }
    }
}
