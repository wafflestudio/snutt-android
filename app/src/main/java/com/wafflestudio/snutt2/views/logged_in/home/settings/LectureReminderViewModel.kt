package com.wafflestudio.snutt2.views.logged_in.home.settings

class LectureReminderViewModel

sealed interface LectureReminderUiState {
    data object Loading : LectureReminderUiState
    data object Error : LectureReminderUiState
    data class Success(val data: Map<String, LectureWithReminderOption>) : LectureReminderUiState
}

data class LectureWithReminderOption(
    val lectureId: String,
    val lectureTitle: String,
    val lectureReminderOffset: LectureReminderOffset,
)

enum class LectureReminderOffset {
    NONE, TEN_MINUTES_BEFORE, AT_START_TIME, TEN_MINUTES_AFTER
}
