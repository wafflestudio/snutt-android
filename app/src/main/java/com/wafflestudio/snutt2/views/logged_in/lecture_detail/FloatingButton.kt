package com.wafflestudio.snutt2.views.logged_in.lecture_detail

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.ArrowLeftBold
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
internal fun FloatingButton(
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(25.dp)

    Row(
        modifier = modifier
            .padding(bottom = 16.dp)
            .shadow(3.dp, shape)
            .background(SNUTTColors.SNUTTTheme, shape)
            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 16.dp)
            .clicks { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        ArrowLeftBold(
            modifier = Modifier.size(23.dp),
            colorFilter = ColorFilter.tint(SNUTTColors.AllWhite),
        )
        Text(
            text = stringResource(R.string.deeplink_page_timetable_lecture_page_floating_button),
            style = SNUTTTypography.h4,
            color = SNUTTColors.AllWhite,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FloatingButtonPreview() {
    FloatingButton(
        modifier = Modifier,
        onClick = {},
    )
}
