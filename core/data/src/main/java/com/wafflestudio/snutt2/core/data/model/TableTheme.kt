package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.theme.BuiltInTheme
import com.wafflestudio.snutt2.core.model.data.theme.CustomTheme
import com.wafflestudio.snutt2.core.model.data.theme.TableTheme
import com.wafflestudio.snutt2.core.network.model.ThemeDto

fun ThemeDto.toExternalModel(): TableTheme {
    return if (isCustom) {
        CustomTheme(
            id = id!!,
            name = name,
            colors = colors.map { it.toExternalModel() },
        )
    } else {
        BuiltInTheme(
            code = theme,
            name = name,
        )
    }
}