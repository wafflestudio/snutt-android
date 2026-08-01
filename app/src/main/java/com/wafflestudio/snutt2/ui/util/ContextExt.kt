package com.wafflestudio.snutt2.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri

fun Context.toast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT,
    ).show()
}

/**
 * Chrome Custom Tab 으로 [url] 을 연다.
 *
 * Custom Tab 을 지원하는 브라우저가 있으면 인앱 형태의 Custom Tab 으로 열리고,
 * 없으면 CustomTabsIntent 내부의 ACTION_VIEW 로 일반 브라우저에 폴백된다.
 * 처리할 수 있는 브라우저가 전혀 없는 경우엔 조용히 무시한다.
 */
fun Context.openCustomTab(url: String) {
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .build()
    try {
        customTabsIntent.launchUrl(this, url.toUri())
    } catch (e: ActivityNotFoundException) {
        // 처리 가능한 브라우저가 없는 경우 무시한다.
    }
}
