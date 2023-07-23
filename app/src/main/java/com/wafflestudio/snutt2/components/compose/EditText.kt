package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
    textStyle: TextStyle = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900),
) {
    val focusManager = LocalFocusManager.current
    LaunchedEffect(clearFocusFlag) {
        if (clearFocusFlag) focusManager.clearFocus()
    }

    var isFocused by remember { mutableStateOf(false) }
    val customTextSelectionColors = TextSelectionColors(
        handleColor = SNUTTColors.Black900,
        backgroundColor = SNUTTColors.Black300
    )
    CompositionLocalProvider(
        LocalTextSelectionColors provides customTextSelectionColors,
    ) {
        BasicTextField(
            modifier = modifier
                .onFocusChanged { isFocused = it.isFocused },
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            value = value,
            textStyle = textStyle,
            enabled = enabled,
            onValueChange = onValueChange,
            singleLine = singleLine,
            visualTransformation = visualTransformation,
            cursorBrush = SolidColor(SNUTTColors.Black900),
            decorationBox = {

                Column {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        leadingIcon()
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (value.isNotEmpty() || isFocused) { // FIXME: lectureDetail 에서는 focus 되어 있어도 empty이면 hint 가 나와야 한다.
                                it()
                            } else {
                                hint?.let {
                                    Text(
                                        text = it,
                                        style = textStyle.copy(color = SNUTTColors.Gray200),
                                    )
                                }
                            }
                        }
                        trailingIcon()
                    }

                    if (underlineEnabled) {
                        Box(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .background(if (isFocused) underlineColorFocused else underlineColor)
                                .fillMaxWidth()
                                .height(underlineWidth)
                        )
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EditTextPreview() {
    var text by remember { mutableStateOf("hello") }
    EditText(value = text, onValueChange = { text = it })
}
