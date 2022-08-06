package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit) = {},
    trailingIcon: @Composable (() -> Unit) = {},
    keyBoardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
) {
    TextField(
        modifier = modifier,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        keyboardOptions = keyBoardOptions,
        keyboardActions = keyboardActions,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            hint?.let {
                Text(text = it, color = SNUTTColors.Gray400, style = SNUTTTypography.subtitle1)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EditTextPreview() {
    var text by remember { mutableStateOf("hello") }
    EditText(value = text, onValueChange = { text = it })
}
