package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
    onSearch: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    TextField(
        modifier = modifier.onFocusChanged {
            isFocused = it.isFocused
        },
        trailingIcon = {
            if (isFocused) ExitIcon()
            else FilterIcon()
        },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            hint?.let {
                Text(text = it, color = SNUTTColors.Gray400, style = SNUTTTypography.subtitle1)
            }
        }
    )
}

@Preview
@Composable
fun EditTextPreview() {
    var text by remember { mutableStateOf("heello") }
    EditText(value = text, onValueChange = { text = it }, onSearch = {})
}
