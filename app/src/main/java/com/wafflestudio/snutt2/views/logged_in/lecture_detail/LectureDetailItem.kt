package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun LectureDetailItem(
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth().height(40.dp),
    value: String = "",
    onValueChange: (String) -> Unit = {},
    hint: String? = stringResource(R.string.lecture_detail_hint_nothing),
    enabled: Boolean = false,
    textStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp),
    focusManager: FocusManager = LocalFocusManager.current,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
    labelVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit = {
        EditText(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            underlineEnabled = false,
            textStyle = textStyle,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            hint = hint,
        )
    }
) {
    Row(
        modifier = modifier,
        verticalAlignment = labelVerticalAlignment,
    ) {
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(color = SNUTTColors.Black600),
            modifier = Modifier.width(88.dp),
            maxLines = 1,
        )
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
        Spacer(modifier = Modifier.width(20.dp))
    }
}
