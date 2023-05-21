package com.wafflestudio.snutt2.views.logged_in.home.share

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.components.compose.ShareImageIcon
import com.wafflestudio.snutt2.components.compose.ShareTableIcon
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.drawer.MoreActionItem

@Composable
fun SharedBottomSheet(
    onShareLink: () -> Unit,
    onShareImage: () -> Unit,
) {
    Column(
        modifier = Modifier
            .background(SNUTTColors.White900)
            .padding(start = 10.dp, top = 12.5.dp, bottom = 6.5.dp)
            .fillMaxWidth()
    ) {
        MoreActionItem(
            Icon = { ShareTableIcon(modifier = Modifier.size(30.dp), isSelected = false) },
            text = "링크로 공유하기",
            onClick = onShareLink,
        )
        MoreActionItem(
            Icon = { ShareImageIcon(modifier = Modifier.size(25.dp)) },
            text = "시간표 이미지 공유하기",
            onClick = onShareImage,
        )
    }
}

fun shareLink(
    context: Context,
    link: String,
) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, link)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
