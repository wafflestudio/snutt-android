package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String,
    positiveButtonText: String = stringResource(R.string.common_ok),
    negativeButtonText: String = stringResource(R.string.common_cancel),
) {
    val screenWidthInDp = with(LocalDensity.current) { LocalView.current.width.toDp() }
    val dialogWidth = min(560.dp, screenWidthInDp - 80.dp)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            elevation = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(min = 280.dp)
                    .width(dialogWidth)
                    .background(SNUTTColors.White900)
                    .padding(top = 36.dp, start = 24.dp, end = 24.dp, bottom = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    style = SNUTTTypography.body1,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = SNUTTColors.LectureDiaryGray,
                                shape = RoundedCornerShape(6.dp),
                            )
                            .clicks { onDismiss() }
                            .padding(vertical = 8.dp),
                        text = negativeButtonText,
                        style = SNUTTTypography.button.copy(color = SNUTTColors.Gray30),
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = SNUTTColors.SNUTTTheme,
                                shape = RoundedCornerShape(6.dp),
                            )
                            .clicks { onConfirm() }
                            .padding(vertical = 8.dp),
                        text = positiveButtonText,
                        style = SNUTTTypography.button.copy(color = SNUTTColors.White900),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    title: String? = null,
    positiveButtonText: String? = stringResource(R.string.common_ok),
    negativeButtonText: String? = stringResource(R.string.common_cancel),
    width: Dp? = null,
    content: @Composable () -> Unit,
) {
    val screenWidthInDp = with(LocalDensity.current) { LocalView.current.width.toDp() }
    val dialogWidth = width ?: min(400.dp, screenWidthInDp - 50.dp)
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            elevation = 10.dp,
        ) {
            Column(
                modifier = Modifier
                    .width(dialogWidth)
                    .background(SNUTTColors.White900),
            ) {
                title?.let {
                    Row {
                        Box(modifier = Modifier.padding(horizontal = 25.dp, vertical = 20.dp)) {
                            Text(
                                text = it,
                                style = SNUTTTypography.h2.copy(fontWeight = FontWeight.Medium),
                            )
                        }
                        Box(modifier = Modifier.weight(1f))
                    }
                }

                Box(modifier = Modifier.padding(horizontal = 25.dp)) {
                    content()
                }

                negativeButtonText?.let {
                    positiveButtonText?.let {
                        Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 30.dp)) {
                            Box(modifier = Modifier.weight(1f))
                            Box(modifier = Modifier.clicks { onDismiss() }) {
                                Text(text = negativeButtonText, style = SNUTTTypography.body1)
                            }
                            Spacer(modifier = Modifier.width(30.dp))
                            Box(modifier = Modifier.clicks { onConfirm() }) {
                                Text(text = positiveButtonText, style = SNUTTTypography.body1)
                            }
                        }
                    }
                } ?: positiveButtonText?.let {
                    Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 30.dp)) {
                        Box(modifier = Modifier.weight(1f))
                        Box(modifier = Modifier.clicks { onConfirm() }) {
                            Text(text = positiveButtonText, style = SNUTTTypography.body1)
                        }
                    }
                }
            }
        }
    }
}
