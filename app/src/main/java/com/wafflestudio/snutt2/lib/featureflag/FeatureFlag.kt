package com.wafflestudio.snutt2.lib.featureflag

import com.wafflestudio.snutt2.BuildConfig

enum class FeatureFlag(
    val isEnabled: Boolean,
) {
    THEME_MARKET(true),
    LECTURE_DIARY(BuildConfig.DEBUG),
    PUSH_PREFERENCES(true),

    LECTURE_REMINDER(true),
}
