package com.wafflestudio.snutt2.ui.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
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

fun Context.openAppLanguageSettings() {
    val packageUri = "package:$packageName".toUri()
    val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)
    val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Intent(Settings.ACTION_APP_LOCALE_SETTINGS, packageUri)
    } else {
        appDetailsIntent
    }

    try {
        startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        if (intent !== appDetailsIntent) {
            try {
                startActivity(appDetailsIntent)
            } catch (_: ActivityNotFoundException) {
                // 처리 가능한 설정 화면이 없는 경우 무시한다.
            }
        }
    }
}

/**
 * Chrome Custom Tab 으로 [url] 을 연다.
 *
 * [darkMode] 로 Custom Tab 툴바의 라이트/다크 색상을 결정한다. 앱은 OS 설정보다
 * 우선하는 자체 테마 설정을 가지므로, 호출부에서 앱 테마 기준값(`isDarkMode()`)을
 * 넘겨 Custom Tab 이 앱 설정과 일치하도록 한다.
 *
 * Custom Tab 을 지원하는 브라우저가 있으면 인앱 형태의 Custom Tab 으로 열리고,
 * 없으면 CustomTabsIntent 내부의 ACTION_VIEW 로 일반 브라우저에 폴백된다.
 * 처리할 수 있는 브라우저가 전혀 없는 경우엔 조용히 무시한다.
 */
fun Context.openCustomTab(url: String, darkMode: Boolean) {
    val colorScheme = if (darkMode) {
        CustomTabsIntent.COLOR_SCHEME_DARK
    } else {
        CustomTabsIntent.COLOR_SCHEME_LIGHT
    }
    val customTabsIntent = CustomTabsIntent.Builder()
        .setShowTitle(true)
        .setColorScheme(colorScheme)
        .build()
    try {
        customTabsIntent.launchUrl(this, url.toUri())
    } catch (e: ActivityNotFoundException) {
        // 처리 가능한 브라우저가 없는 경우 무시한다.
    }
}
