package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun DividerWithText(
    thickness: Dp = 1.dp,
    color: Color,
    text: String = "",
    textStyle: TextStyle,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ){
        Divider(
            modifier = Modifier.weight(1f),
            color = color,
            thickness = thickness,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            style = textStyle,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Divider(
            modifier = Modifier.weight(1f),
            color = color,
            thickness = thickness,
        )
    }
}