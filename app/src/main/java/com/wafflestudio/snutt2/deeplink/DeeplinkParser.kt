package com.wafflestudio.snutt2.deeplink

import android.net.Uri
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.views.NavigationDestination

object DeeplinkParser {
    fun parseDeeplink(rawDeeplink: String?): DeeplinkAction {
        if (rawDeeplink == null) {
            return DeeplinkAction.DeeplinkNoAction
        }

        // scheme 떼내기
        val rawUri = Uri.parse(rawDeeplink)
        val uri = Uri.Builder().apply {
            authority(rawUri.host)
            path(rawUri.path)
            query(rawUri.query)
        }.build()

        return when (uri.host) {
            DeeplinkPath.TimetableLecture -> {
                DeeplinkAction.DeeplinkNavigationAction(
                    Uri.decode(Uri.Builder().apply {
                        path(NavigationDestination.Home)
                        query(uri.query)
                    }.build().toString())
                )
            }
            DeeplinkPath.Bookmarks -> {
                DeeplinkAction.DeeplinkNavigationAction(
                    Uri.decode(Uri.Builder().apply {
                        path(NavigationDestination.Home)
                        query(uri.query)
                    }.build().toString())
                )
            }

            else -> DeeplinkAction.DeeplinkNoAction
        }
    }
}

object DeeplinkPath {
    const val TimetableLecture = "timetable-lecture"
    const val Bookmarks = "bookmarks"
}
