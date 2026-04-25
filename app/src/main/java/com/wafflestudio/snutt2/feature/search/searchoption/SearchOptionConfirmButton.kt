package com.wafflestudio.snutt2.feature.search.searchoption

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun SearchOptionConfirmButton(
    baseAnimatedFloat: State<Float>,
    onConfirm: () -> Unit,
) {
    val alphaAnimatedFloat by remember {
        derivedStateOf { 1f - baseAnimatedFloat.value }
    }
    val offsetYAnimatedDp by remember {
        derivedStateOf {
            baseAnimatedFloat.value.dp * 500 // FIXME
        }
    }

    Row(
        modifier = Modifier
            .offset { IntOffset(0, offsetYAnimatedDp.roundToPx()) }
            .alpha(alphaAnimatedFloat)
            .background(SNUTTColors.Sky)
            .fillMaxWidth()
            .height(60.dp)
            .clicks(enabled = alphaAnimatedFloat != 0f) {
                onConfirm()
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(R.string.search_option_apply_button),
            textAlign = TextAlign.Center,
            style = SNUTTTypography.h3.copy(fontSize = 17.sp, color = SNUTTColors.AllWhite),
        )
    }
}

@SnuttPreview
@Composable
private fun SearchOptionConfirmButton_Default() {
    val baseAnimatedFloat = remember { mutableFloatStateOf(0f) }
    SnuttPreviewSurface {
        SearchOptionConfirmButton(
            baseAnimatedFloat = baseAnimatedFloat,
            onConfirm = {},
        )
    }
}
