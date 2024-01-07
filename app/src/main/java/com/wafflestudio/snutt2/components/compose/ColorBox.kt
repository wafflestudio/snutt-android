package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun ColorBox(
    color: ColorDto,
) {
    Row(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp)
            .border(width = (0.5f).dp, color = SNUTTColors.Black250),
    ) {
        Box(
            modifier = Modifier
                .background(
                    Color(color.fgColor ?: 0xffffff),
                )
                .size(20.dp),
        )
        Box(
            modifier = Modifier
                .background(
                    Color(color.bgColor ?: 0xffffff),
                )
                .size(20.dp),
        )
    }
}
