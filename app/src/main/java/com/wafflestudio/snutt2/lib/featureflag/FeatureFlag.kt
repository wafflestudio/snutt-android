package com.wafflestudio.snutt2.lib.featureflag

enum class FeatureFlag(
    val isEnabled: Boolean,
) {
    THEME_MARKET(true),
    LECTURE_DIARY(true),
    PUSH_PREFERENCES(true),
}
