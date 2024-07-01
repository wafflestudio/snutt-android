package com.wafflestudio.snutt2.core.network.util

import android.os.Handler
import android.os.Looper

fun runOnUiThread(runnable: () -> Unit) {
    Handler(Looper.getMainLooper())
        .post(runnable)
}
