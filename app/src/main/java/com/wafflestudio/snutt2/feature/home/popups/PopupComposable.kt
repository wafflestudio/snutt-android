package com.wafflestudio.snutt2.feature.home.popups

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
fun Popup(
    onClickFewDays: () -> Unit,
    onClickClose: () -> Unit,
    imageContent: @Composable () -> Unit,
) {
    val rawWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }
    val containerWidthDp = if (rawWidth > 0.dp) rawWidth else 360.dp
    val imageWidth = min(containerWidthDp * 0.8f, 400.dp)

    Box(
        modifier = Modifier
            .zIndex(2f)
            .fillMaxSize()
            .background(SNUTTColors.Dim2)
            .clicks {},
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.width(imageWidth),
        ) {
            imageContent()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(id = R.string.popup_hide_message),
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .weight(3f)
                        .clicks { onClickFewDays() },
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    color = SNUTTColors.AllWhite,
                )
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .height(17.dp)
                        .background(SNUTTColors.AllWhite),
                )
                Text(
                    text = stringResource(id = R.string.popup_close_message),
                    modifier =
                    Modifier
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp,
                        )
                        .weight(2f)
                        .clicks { onClickClose() },
                    textAlign = TextAlign.Center,
                    color = SNUTTColors.AllWhite,
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun Popup_Default() {
    SnuttPreviewSurface {
        Popup(onClickFewDays = {}, onClickClose = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(SNUTTColors.Gray400),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Preview Image",
                    color = SNUTTColors.Gray900,
                )
            }
        }
    }
}
