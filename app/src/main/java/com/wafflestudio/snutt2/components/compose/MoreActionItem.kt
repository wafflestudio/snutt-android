package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.ui.isDarkMode

@Composable
fun MoreActionItem(
    icon: @Composable () -> Unit,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .clicks { if (enabled) onClick() }
            .padding(vertical = 10.dp, horizontal = 22.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            icon()
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = text,
                style = SNUTTTypography.body1.copy(
                    color = if (enabled) {
                        MaterialTheme.colors.onSurface
                    } else {
                        if (isDarkMode()) SNUTTColors.DarkGray else SNUTTColors.Gray2
                    },
                ),
            )
        }
    }
}
