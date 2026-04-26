package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun EditTextFieldValue(
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit) = {},
    trailingIcon: @Composable (() -> Unit) = {},
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    enabled: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    hint: String? = null,
    hintTextColor: Color = SNUTTColors.EditTextHint,
    hintTextStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
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
        backgroundColor = SNUTTColors.Black300,
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
                            modifier = Modifier.weight(1f),
                        ) {
                            it()
                            if (value.text.isEmpty()) {
                                hint?.let {
                                    Text(
                                        text = it,
                                        style = hintTextStyle.copy(color = hintTextColor),
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
                                .height(underlineWidth),
                        )
                    }
                }
            },
        )
    }
}

@SnuttPreview
@Composable
private fun EditTextFieldValue_Filled() {
    SnuttPreviewSurface {
        EditTextFieldValue(
            value = TextFieldValue("hello"),
            onValueChange = {},
        )
    }
}

@SnuttPreview
@Composable
private fun EditTextFieldValue_EmptyWithHint() {
    SnuttPreviewSurface {
        EditTextFieldValue(
            value = TextFieldValue(""),
            onValueChange = {},
            hint = "이름을 입력해주세요",
        )
    }
}
