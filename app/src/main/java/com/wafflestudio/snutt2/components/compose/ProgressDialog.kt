package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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

@Composable
fun CustomDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String = "확인",
    dismissButtonText: String = "취소",
    title: String,
    content: @Composable (() -> Unit)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Text(
                text = confirmButtonText,
                modifier = Modifier
                    .clicks {
                        onConfirm()
                    }
                    .padding(20.dp)
            )
        },
        dismissButton = {
            Text(
                text = dismissButtonText,
                modifier = Modifier
                    .clicks {
                        onDismiss()
                    }
                    .padding(20.dp)
            )
        },
        title = { Text(text = title) },
        text = content,
    )
}
