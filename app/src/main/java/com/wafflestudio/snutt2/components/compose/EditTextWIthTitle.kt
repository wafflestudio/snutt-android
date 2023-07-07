package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun EditTextWithTitle(
    modifier: Modifier = Modifier,
    title: String,
    titleStyle: TextStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black600),
    leadingIcon: @Composable (() -> Unit) = {},
    trailingIcon: @Composable (() -> Unit) = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
    underlineEnabled: Boolean = true,
    underlineColor: Color = SNUTTColors.Gray200,
    underlineColorFocused: Color = SNUTTColors.Black900,
    underlineWidth: Dp = 1.dp,
    clearFocusFlag: Boolean = false,
    textStyle: TextStyle = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Black900),
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title,
            style = titleStyle
        )
        EditText(
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            singleLine = singleLine,
            enabled = enabled,
            visualTransformation = visualTransformation,
            value = value,
            onValueChange = onValueChange,
            hint = hint,
            underlineEnabled = underlineEnabled,
            underlineColor = underlineColor,
            underlineColorFocused = underlineColorFocused,
            underlineWidth = underlineWidth,
            clearFocusFlag = clearFocusFlag,
            textStyle = textStyle
        )
    }
}
