package com.wafflestudio.snutt2.components.compose

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
    value: String,
    onValueChange: (String) -> Unit,
    hint: String? = null
) {
    TextField(
        modifier = modifier,
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
    EditText(value = text, onValueChange = { text = it })
}
