package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewSubmitButton(
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(122.dp)
            .height(42.dp)
            .background(
                if (enabled) SNUTTColors.MainBlue else SNUTTColors.LineLight,
                RoundedCornerShape(6.dp),
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.review_submit),
            style = SNUTTTypography.h3.copy(
                fontSize = 15.sp,
                color = if (enabled) Color.White else SNUTTColors.TextAssistive,
            ),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewSubmitButton_Enabled() {
    SnuttPreviewCenteredSurface {
        ReviewSubmitButton(enabled = true, onClick = {})
    }
}
