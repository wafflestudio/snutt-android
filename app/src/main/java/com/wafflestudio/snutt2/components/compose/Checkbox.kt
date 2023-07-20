package com.wafflestudio.snutt2.components.compose

import androidx.compose.material.CheckboxColors
import androidx.compose.material.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun Checkbox1(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    colors: CheckboxColors = CheckboxDefaults.colors(
        checkedColor = SNUTTColors.SNUTTTheme
    )
) {

}
