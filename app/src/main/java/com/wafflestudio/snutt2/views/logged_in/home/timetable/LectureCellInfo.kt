package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.ui.text.TextStyle

data class LectureCellInfo(
    val text: String,
    val enabled: Boolean,
    val style: TextStyle,
    val minifiedStyle: TextStyle,
) {
    companion object {
        fun titleTextLayout(title: String, enabled: Boolean) = LectureCellInfo(title, enabled, TimetableCanvasObjects.lectureTitleTextStyle, TimetableCanvasObjects.lectureTitleMinifiedTextStyle)
        fun placeTextLayout(place: String, enabled: Boolean) = LectureCellInfo(place, enabled, TimetableCanvasObjects.lecturePlaceTextStyle, TimetableCanvasObjects.lecturePlaceMinifiedTextStyle)
        fun lectureNumberTextLayout(lectureNumber: String, enabled: Boolean) = LectureCellInfo(lectureNumber, enabled, TimetableCanvasObjects.lectureNumberTextStyle, TimetableCanvasObjects.lectureNumberMinifiedTextStyle)
        fun instructorNameTextLayout(name: String, enabled: Boolean) = LectureCellInfo(name, enabled, TimetableCanvasObjects.lectureInstructorTextStyle, TimetableCanvasObjects.lectureInstructorMinifiedTextStyle)
    }
}
