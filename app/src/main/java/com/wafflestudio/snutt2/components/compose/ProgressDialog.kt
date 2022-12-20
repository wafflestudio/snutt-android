package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun ProgressDialog(
    title: String,
    message: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            modifier = Modifier
                .background(Color.White)
                .fillMaxWidth(0.98f)
                .padding(20.dp)
        ) {
            Column {
                Text(title)
                Spacer(modifier = Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.width(30.dp))
                    Text(message)
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
    positiveButtonText: String = stringResource(R.string.common_ok),
    negativeButtonText: String = stringResource(R.string.common_cancel),
    content: @Composable () -> Unit
) {
    val screenWidthInDp = with(LocalDensity.current) { LocalView.current.width.toDp() }
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface {
            Column(modifier = Modifier.width(screenWidthInDp - 50.dp)) {
                title?.let {
                    Row {
                        Box(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = it,
                                style = SNUTTTypography.h2.copy(fontWeight = FontWeight.Normal)
                            )
                        }
                        Box(modifier = Modifier.weight(1f))
                    }
                }

                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    content()
                }

                Row(modifier = Modifier.padding(vertical = 20.dp, horizontal = 30.dp)) {
                    Box(modifier = Modifier.weight(1f))
                    Box(modifier = Modifier.clicks { onDismiss() }) {
                        Text(text = negativeButtonText, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(30.dp))
                    Box(modifier = Modifier.clicks { onConfirm() }) {
                        Text(text = positiveButtonText, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
