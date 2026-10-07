package com.wafflestudio.snutt2.feature.home

sealed class HomeItem {

    object Timetable : HomeItem()
    object Search : HomeItem()
    object Review : HomeItem()
    object Friends : HomeItem()
    object Settings : HomeItem()

    fun toTabString(): String = when (this) {
        is Timetable -> "timetable"
        is Search -> "search"
        is Review -> "review"
        is Friends -> "friends"
        is Settings -> "settings"
    }

    companion object {
        fun fromTabString(value: String?): HomeItem? = when (value) {
            "timetable" -> Timetable
            "search" -> Search
            "review" -> Review
            "friends" -> Friends
            "settings" -> Settings
            else -> null
        }
    }
}
