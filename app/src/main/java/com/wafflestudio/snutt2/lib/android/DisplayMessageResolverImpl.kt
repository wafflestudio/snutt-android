package com.wafflestudio.snutt2.lib.android

import android.content.Context
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.BookmarkLectureNotFound
import com.wafflestudio.snutt2.domain.DisplayMessageResolver
import com.wafflestudio.snutt2.domain.DomainError
import com.wafflestudio.snutt2.domain.NetworkDisconnect
import com.wafflestudio.snutt2.domain.NotSelectedTimetable
import com.wafflestudio.snutt2.domain.TimetableLectureNotFound
import com.wafflestudio.snutt2.domain.Unknown
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// DisplayMessageResolver는 서버 displayMessage가 없거나, 있는데 클라이언트의 displayMessage와 다른 경우를 커버한다.
// 추후 테스트를 용이하게 하기 위해 interface 분리되어 있음.
class DisplayMessageResolverImpl @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : DisplayMessageResolver {
    override fun getDisplayTitle(error: DomainError): String = when (error) {
        is NetworkDisconnect -> context.getString(R.string.error_title_no_network)
        is Unknown -> error.displayTitle
        else -> error.displayTitle
    }
    override fun getDisplayMessage(error: DomainError): String = when (error) {
        is NetworkDisconnect -> context.getString(R.string.error_no_network)
        is TimetableLectureNotFound -> context.getString(R.string.deeplink_page_timetable_lecture_page_not_existing_lecture)
        is BookmarkLectureNotFound -> context.getString(R.string.deeplink_page_timetable_lecture_page_not_existing_bookmark_lecture)
        is NotSelectedTimetable -> context.getString(R.string.home_drawer_change_theme_unable_alert_message)
        is Unknown -> error.displayMessage.takeUnless { it.isBlank() } ?: context.getString(R.string.error_unknown)
        else -> error.displayMessage
    }
}
