package com.wafflestudio.snutt2.components.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

/**
 * 여러 선택지 중 하나를 선택할 수 있는 UI 컴포넌트
 *
 * @param title 컴포넌트의 제목
 * @param options 선택 가능한 항목들의 문자열 리스트
 * @param selectedOption 현재 선택된 항목
 * @param onOptionSelected 사용자가 새로운 항목을 선택했을 때 호출되는 콜백 함수
 * @param modifier 이 컴포저블에 적용할 Modifier
 */
@Composable
fun SegmentedPicker(
    title: String?,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(top = 18.dp)
            .padding(bottom = 16.dp),
    ) {
        if (!title.isNullOrEmpty()) {
            Text(
                modifier = Modifier.padding(horizontal = 4.dp),
                text = title,
                style = SNUTTTypography.body1.copy(fontSize = 15.sp),
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(32.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(SNUTTColors.FillTertiary)
                .padding(2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, option ->
                val isSelected = selectedOption == option
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(7.dp))
                        .background(
                            if (isSelected) {
                                SNUTTColors.BackgroundPrimary
                            } else {
                                Color.Transparent
                            },
                        )
                        .then(
                            if (isSelected) {
                                Modifier
                                    .border(0.5.dp, SNUTTColors.Black150, RoundedCornerShape(7.dp))
                            } else {
                                Modifier
                            },
                        )
                        .clicks { onOptionSelected(option) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = option,
                        textAlign = TextAlign.Center,
                        style = SNUTTTypography.body2.copy(fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium),
                    )
                }
                // 마지막 아이템이 아닐 경우에만 구분선 추가
                if (index < options.lastIndex) {
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(vertical = 6.dp)
                            .width(0.5.dp),
                        color = SNUTTColors.SeparatorTransparency,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SegmentedPickerPreview() {
    val reminderOptions = listOf("없음", "10분 전", "수업 시작 시", "10분 후")
    Column(
        modifier = Modifier.width(374.dp),
    ) {
        SegmentedPicker(
            title = "강의 리마인더",
            options = reminderOptions,
            selectedOption = "없음",
            onOptionSelected = { _ -> },
        )
    }
}
