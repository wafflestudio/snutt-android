package com.wafflestudio.snutt2.views.logged_in.home.settings.diary

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.material.color.utilities.MaterialDynamicColors.background
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography

@Composable
fun DiaryCompleteRoute(
    modifier: Modifier = Modifier,
    onNavigateNextPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
) {
    DiaryCompleteScreen(
        diaryCompleteState = DiaryCompleteState.MoreDiary, // TODO: 처리 필요
        onNavigateNextPage = onNavigateNextPage,
        onNavigateHomePage = onNavigateHomePage,
    )
}

@Composable
fun DiaryCompleteScreen(
    modifier: Modifier = Modifier,
    diaryCompleteState: DiaryCompleteState,
    onNavigateNextPage: () -> Unit,
    onNavigateHomePage: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(start = 32.dp, end = 32.dp, bottom = 40.dp, top = 248.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 45.dp)
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_cat_complete),
                contentDescription = "",
            )
            Text(
                modifier = Modifier.padding(top = 24.dp),
                text = "강의일기가 등록되었습니다.",
                style = SNUTTTypography.h3.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = "작성한 강의일기는 더보기>강의일기장에서 확인할 수 있어요.",
                style = SNUTTTypography.body1.copy(color = SNUTTColors.Black.copy(alpha = 0.5f)),
                textAlign = TextAlign.Center,
            )
            if (diaryCompleteState != DiaryCompleteState.NoMoreDiary) {
                Box(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .border(
                            width = 1.dp,
                            color = SNUTTColors.Gray,
                            shape = RoundedCornerShape(30.dp),
                        )
                        .background(color = Color.Transparent, shape = RoundedCornerShape(30.dp))
                        .clicks { onNavigateNextPage() }
                        .padding(start = 29.dp, end = 21.dp, top = 13.dp, bottom = 13.dp),
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = when (diaryCompleteState) {
                                DiaryCompleteState.MoreDiary -> "더 기록하기"
                                DiaryCompleteState.LectureReview -> "강의평 남기기"
                                DiaryCompleteState.NoMoreDiary -> ""
                            },
                            style = SNUTTTypography.button.copy(fontSize = 15.sp),
                        )
                        Icon(modifier = Modifier.size(20.dp, 20.dp), imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_right), contentDescription = "")
                    }
                }
            }
        }

        Text(
            modifier = Modifier.clicks { onNavigateHomePage() }
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(color = SNUTTColors.SNUTTTheme, shape = RoundedCornerShape(6.dp))
                .padding(12.dp),
            text = "홈으로",
            textAlign = TextAlign.Center,
            style = SNUTTTypography.button.copy(color = SNUTTColors.White, fontSize = 15.sp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DiaryCompletePreview() {
    DiaryCompleteScreen(
        diaryCompleteState = DiaryCompleteState.MoreDiary,
        onNavigateNextPage = {},
        onNavigateHomePage = {},
    )
}

@Composable
@Preview(showBackground = true)
fun DiaryCompleteNoMoreDiaryPreview() {
    DiaryCompleteScreen(
        diaryCompleteState = DiaryCompleteState.NoMoreDiary,
        onNavigateNextPage = {},
        onNavigateHomePage = {},
    )
}

sealed class DiaryCompleteState {
    data object MoreDiary : DiaryCompleteState()
    data object NoMoreDiary : DiaryCompleteState()
    data object LectureReview : DiaryCompleteState()
}
