package com.wafflestudio.snutt2.feature.lecturedetail

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun LectureDetailItem(
    title: String,
    modifier: Modifier = Modifier.fillMaxWidth().height(40.dp),
    value: String = "",
    onValueChange: (String) -> Unit = {},
    hint: String? = stringResource(R.string.lecture_detail_hint_nothing),
    enabled: Boolean = false,
    editable: Boolean = true,
    textStyle: TextStyle = SNUTTTypography.body1.copy(fontSize = 15.sp, color = if (editable) SNUTTColors.Black900 else SNUTTColors.Gray600),
    focusManager: FocusManager = LocalFocusManager.current,
    singleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    keyboardActions: KeyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
    labelVerticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable () -> Unit = {
        if (enabled) {
            EditText(
                value = value,
                onValueChange = onValueChange,
                enabled = true,
                modifier = Modifier.fillMaxWidth(),
                singleLine = singleLine,
                underlineEnabled = false,
                textStyle = textStyle,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                hint = hint,
            )
        } else {
            Text(
                text = value.ifBlank { hint ?: "" },
                style = if (value == "") textStyle.copy(color = SNUTTColors.Gray200) else textStyle,
                maxLines = if (singleLine) 1 else Int.MAX_VALUE,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    },
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
