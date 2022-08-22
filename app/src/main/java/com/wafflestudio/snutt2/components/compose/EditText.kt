package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyBoardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        keyboardActions = keyboardActions,
        keyboardOptions = keyBoardOptions,
        enabled = enabled,
        readOnly = readOnly,
        modifier = modifier
    )

//    TextField(
//        modifier = modifier,
//        leadingIcon = leadingIcon,
//        trailingIcon = trailingIcon,
//        keyboardOptions = keyBoardOptions,
//        keyboardActions = keyboardActions,
//        value = value,
//        onValueChange = onValueChange,
//        placeholder = {
//            hint?.let {
//                Text(text = it, color = SNUTTColors.Gray400, style = SNUTTTypography.subtitle1)
//            }
//        }
//    )
}

@Preview(showBackground = true)
@Composable
fun EditTextPreview() {
    var text by remember { mutableStateOf("hello") }
    EditText(value = text, onValueChange = { text = it })
}
