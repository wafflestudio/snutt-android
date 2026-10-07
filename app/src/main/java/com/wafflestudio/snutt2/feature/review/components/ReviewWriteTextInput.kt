package com.wafflestudio.snutt2.feature.review.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun ReviewTextInput(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var focused by remember { mutableStateOf(false) }
    val valid = text.length >= 20
    val placeholder = stringResource(R.string.review_input_placeholder)
    val characterCount = stringResource(R.string.review_input_count, text.length)
    val minimumCount = stringResource(R.string.review_input_min_count)
    val characterCountColor = if (valid) SNUTTColors.MainBlue else SNUTTColors.TextPlain
    val minimumCountColor = SNUTTColors.TextAssistive
    val borderColor = when {
        valid -> SNUTTColors.MainBlue
        focused -> Color(0xffa6a6a6)
        else -> SNUTTColors.LineLight
    }

    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        EditText(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .onFocusChanged { focused = it.isFocused }
                .border(1.dp, borderColor, RoundedCornerShape(6.dp))
                .background(SNUTTColors.White900, RoundedCornerShape(6.dp))
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 4.dp),
            textStyle = SNUTTTypography.body1.copy(color = SNUTTColors.Black900),
            hint = if (focused) null else placeholder,
            hintTextColor = SNUTTColors.Gray30,
            hintTextStyle = SNUTTTypography.body1,
            underlineEnabled = false,
        )

        Text(
            text = buildAnnotatedString {
                if (text.isNotEmpty() || focused) {
                    withStyle(
                        SpanStyle(
                            color = characterCountColor,
                            fontWeight = FontWeight.Medium,
                        ),
                    ) {
                        append(characterCount)
                        append(" ")
                    }
                }
                withStyle(SpanStyle(color = minimumCountColor)) {
                    append(minimumCount)
                }
            },
            style = SNUTTTypography.body2.copy(fontSize = 13.sp),
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@SnuttPreview
@Composable
private fun ReviewTextInput_Empty() {
    SnuttPreviewCenteredSurface {
        ReviewTextInput("", {})
    }
}
