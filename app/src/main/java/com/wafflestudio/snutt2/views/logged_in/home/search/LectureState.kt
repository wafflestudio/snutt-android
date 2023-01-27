package com.wafflestudio.snutt2.views.logged_in.home.search

data class LectureState(
    val selected: Boolean,
    val contained: Boolean,
    val bookmarked: Boolean = false,
)
