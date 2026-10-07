package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewFilterTag(
    label: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(20.dp)
    val color = SNUTTColors.DarkMintText
    Row(
        modifier = modifier
            .height(30.dp)
            .background(Color(0x0f00b8b0), shape)
            .border(0.5.dp, Color(0xff00b8b0), shape)
            .clickable(onClick = onRemove)
            .padding(start = 14.dp, end = 9.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, style = SNUTTTypography.body1.copy(color = color))
        Image(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.review_filter_remove, label),
            colorFilter = ColorFilter.tint(color),
            modifier = Modifier.size(15.dp),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewFilterTag_Default() {
    SnuttPreviewCenteredSurface {
        ReviewFilterTag("1학년", {})
    }
}
