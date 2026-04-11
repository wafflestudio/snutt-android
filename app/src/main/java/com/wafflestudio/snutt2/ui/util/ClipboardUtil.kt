package com.wafflestudio.snutt2.ui.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import com.wafflestudio.snutt2.R

fun copyToClipboard(
    context: Context,
    content: String,
    toastMessage: String? = null,
) {
    val clipboardManager = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val clip = ClipData.newPlainText("label", content)
    clipboardManager.setPrimaryClip(clip)

    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
        context.toast(toastMessage ?: context.getString(R.string.common_copied_to_clipboard))
    }
}
