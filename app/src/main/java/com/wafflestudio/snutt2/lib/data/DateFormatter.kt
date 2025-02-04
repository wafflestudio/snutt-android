package com.wafflestudio.snutt2.lib.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    // Full Pattern: 서버 응답 형태로, ms 단위까지 표현되어 있는 패턴
    private const val FULLPATTERN: String = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private val threadLocalFullFormatter = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat(FULLPATTERN, Locale.getDefault())
        }
    }

    fun parseFull(dateString: String): Date {
        return threadLocalFullFormatter.get()?.parse(dateString) ?: Date()
    }

    // Date Pattern: UI 표현 중 하나로, 날짜만 표기되어 있는 패턴
    private const val DATEPATTERN: String = "yyyy/MM/dd"

    private val threadLocalDateFormatter = object : ThreadLocal<SimpleDateFormat>() {
        override fun initialValue(): SimpleDateFormat {
            return SimpleDateFormat(DATEPATTERN, Locale.getDefault())
        }
    }

    fun formatDate(date: Date): String {
        return threadLocalDateFormatter.get()?.format(date) ?: ""
    }
}
