package com.wafflestudio.snutt2.ui.util

import com.wafflestudio.snutt2.domain.model.TagType
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

fun TagType.color(): androidx.compose.ui.graphics.Color {
    return when (this) {
        TagType.SORT_CRITERIA -> SNUTTColors.Gray30
        TagType.CLASSIFICATION -> SNUTTColors.Red
        TagType.DEPARTMENT -> SNUTTColors.Orange
        TagType.ACADEMIC_YEAR -> SNUTTColors.Grass
        TagType.CREDIT -> SNUTTColors.Sky
        TagType.TIME -> SNUTTColors.Blue
        TagType.CATEGORY -> SNUTTColors.NavyBlue
        TagType.CATEGORY_PRE2025 -> SNUTTColors.NavyBlue
        TagType.ETC -> SNUTTColors.Violet
    }
}
