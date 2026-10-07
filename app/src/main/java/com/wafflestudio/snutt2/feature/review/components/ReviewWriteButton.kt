package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewWriteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 17.dp,
) {
    Row(
        modifier = modifier
            .background(SNUTTColors.MainBlue, RoundedCornerShape(cornerRadius))
            .clicks(onClick = onClick)
            .padding(start = 12.dp, end = 14.dp, top = 8.dp, bottom = 7.2.dp),
        horizontalArrangement = Arrangement.spacedBy(3.2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_write),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.White),
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = stringResource(R.string.review_write),
            style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewWriteButton_Default() {
    SnuttPreviewCenteredSurface {
        ReviewWriteButton(onClick = {})
    }
}

@SnuttPreview
@Composable
private fun ReviewWriteButton_Home() {
    SnuttPreviewCenteredSurface {
        ReviewWriteButton(onClick = {}, cornerRadius = 4.dp)
    }
}
