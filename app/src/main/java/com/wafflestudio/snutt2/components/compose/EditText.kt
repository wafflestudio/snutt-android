package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun EditText(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit) = {},
    trailingIcon: @Composable (() -> Unit) = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null,
) {
    var isFocused by remember { mutableStateOf(false) }

    BasicTextField(
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused },
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        value = value,
        textStyle = SNUTTTypography.subtitle1.copy(
            color = SNUTTColors.Black900,
        ),
        onValueChange = onValueChange,
        singleLine = singleLine,
        visualTransformation = visualTransformation,
        decorationBox = {

            Column {
                Row(modifier = Modifier.fillMaxWidth()) {
                    leadingIcon()
                    if (value.isNotEmpty() || isFocused) {
                        it()
                    } else {
                        hint?.let {
                            Text(
                                text = it,
                                color = SNUTTColors.Gray200,
                                style = SNUTTTypography.subtitle1
                            )
                        }
                    }
                    trailingIcon()
                }

                Box(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .background(if (isFocused) SNUTTColors.Black900 else SNUTTColors.Gray200)
                        .fillMaxWidth()
                        .height(1.dp)
                )
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
