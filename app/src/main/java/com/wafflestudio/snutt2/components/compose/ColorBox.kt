package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.lib.network.dto.core.ThemeDto
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.ui.SNUTTColors

@Composable
fun ColorBox(
    lectureColorIndex: Long,
    lectureColor: ColorDto?, // null 이면 반드시 기존 테마.
    theme: ThemeDto?,
) {
    Row(
        modifier = Modifier
            .width(40.dp)
            .height(20.dp)
            .border(width = (0.5f).dp, color = SNUTTColors.Black250),
    ) {
        Box(
            modifier = Modifier
                .background(
                    // colorIndex == 0 이면 사용자 커스텀 색
                    // colorIndex > 0 이면 bgColor 는 스누티티 지정 테마 색깔, fgColor = -0x1 (디폴트 흰색)
                    if ((lectureColorIndex) > 0) {
                        Color(-0x1)
                    } // 커스텀 fg 색이면 null 이 오지 않아서 원래는 !! 처리했지만..
                    else {
                        Color(lectureColor?.fgColor ?: -0x1)
                    },
                )
                .size(20.dp),
        )
        Box(
            modifier = Modifier
                .background(
                    // index > 0 : 스누티티 지정 테마 색깔.
                    if (lectureColorIndex > 0) {
                        theme!!.getBuiltInColorByIndex(lectureColorIndex)
                    } // 사용자 지정 bgColor, 역시 이때는 null이 오지 않아서 !! 처리를 했었다. 그냥 !! 해도 될지도
                    else {
                        Color(lectureColor?.bgColor ?: (-0x1))
                    },
                )
                .size(20.dp),
        )
    }
}
