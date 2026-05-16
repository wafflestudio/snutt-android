package com.wafflestudio.snutt2.config

enum class FeatureFlag(
    val isEnabled: Boolean,
) {
    THEME_MARKET(true),
    LECTURE_DIARY(true),
    PUSH_PREFERENCES(true),

    LECTURE_REMINDER(true),
}
