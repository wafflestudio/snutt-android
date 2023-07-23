package com.wafflestudio.snutt2.components.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun RoundCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    checkedBgColor: Color = SNUTTColors.Black900,
    uncheckedBgColor: Color = SNUTTColors.Transparent,
    checkMarkColor: Color = SNUTTColors.White900,
    borderColor: Color = SNUTTColors.CheckboxBorder
) {
    val animatedBgColor: Color by animateColorAsState(if (checked) checkedBgColor else uncheckedBgColor)
    val animatedBorderColor: Color by animateColorAsState(if (checked) checkedBgColor else borderColor)
    Box(
        modifier = modifier
            .clip(CircleShape)
            .size(20.dp)
            .border(width = 2.dp, color = animatedBorderColor, shape = CircleShape)
            .background(animatedBgColor)
            .clicks {
                if (onCheckedChange != null) {
                    onCheckedChange(!checked)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = checked,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Icon(
                modifier = Modifier.padding(4.dp),
                imageVector = Icons.Default.Check,
                tint = checkMarkColor,
                contentDescription = null
            )
        }
    }
}
