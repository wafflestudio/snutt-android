package com.wafflestudio.snutt2.deeplink

import androidx.core.net.toUri
import com.wafflestudio.snutt2.lib.SNUTTUtils.semesterStringToLong
import com.wafflestudio.snutt2.navigation.getDeepLinkPath
import com.wafflestudio.snutt2.views.NavigationDestination
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed interface DeeplinkAction {
    data class TimetableLecture(
        val timetableId: String,
        val lectureId: String,
    ) : DeeplinkAction

    data class Bookmark(
        val year: Long,
        val semester: Long,
        val lectureId: String,
    ) : DeeplinkAction

    data object Friends : DeeplinkAction
}

@Singleton
class DeeplinkParser @Inject constructor(
    @param:Named("AppScheme") private val appScheme: String,
) {
    fun parse(deeplink: String): DeeplinkAction? {
        if (!deeplink.startsWith(appScheme)) return null
        val uri = deeplink.toUri()

        return when (uri.host) {
            getDeepLinkPath<NavigationDestination.TimetableLecture>() -> {
                val timetableId = uri.getQueryParameter("timetableId") ?: return null
                val lectureId = uri.getQueryParameter("lectureId") ?: return null
                DeeplinkAction.TimetableLecture(timetableId, lectureId)
            }

            getDeepLinkPath<NavigationDestination.Bookmark>() -> {
                val year = uri.getQueryParameter("year")?.toLongOrNull() ?: return null
                val semester = uri.getQueryParameter("semester")?.semesterStringToLong() ?: return null
                val lectureId = uri.getQueryParameter("lectureId") ?: return null
                DeeplinkAction.Bookmark(year, semester, lectureId)
            }

            getDeepLinkPath<NavigationDestination.Friends>() -> {
                DeeplinkAction.Friends
            }

            else -> null
        }
    }
}
