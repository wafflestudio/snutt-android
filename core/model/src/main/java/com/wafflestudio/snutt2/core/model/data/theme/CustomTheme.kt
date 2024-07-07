package com.wafflestudio.snutt2.core.model.data.theme

import com.wafflestudio.snutt2.core.model.data.LectureColor

data class CustomTheme(
    val id: String,
    override val name: String,
    val colors: List<LectureColor>,
) : TableTheme(name) {

    companion object {
        val Default = CustomTheme(
            id = "",
            name = "새 커스텀 테마",
            colors = listOf(LectureColor(foreground = 0xffffff, background = 0x1bd0c8)),
        )
    }
}
