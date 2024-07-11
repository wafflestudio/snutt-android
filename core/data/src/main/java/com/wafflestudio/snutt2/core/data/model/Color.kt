package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.LectureColor
import com.wafflestudio.snutt2.core.network.model.ColorDto

fun ColorDto.toExternalModel() = LectureColor(
    foregroundString = fgRaw ?: "#000000",
    backgroundString = bgRaw ?: "#FFFFFF",
)
// TODO : 검은 바탕에 흰 글씨를 default로 해놨는데, 추후 revisit