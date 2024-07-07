package com.wafflestudio.snutt2.core.model.data

enum class ThemeMode {
    DARK,
    LIGHT,
    AUTO;

    override fun toString(): String {
        return when (this) {
            DARK -> "다크"
            LIGHT -> "라이트"
            AUTO -> "자동"
        }
    }
}