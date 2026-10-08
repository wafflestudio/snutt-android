package com.wafflestudio.snutt2.feature.review

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.review.ReviewedCourse
import com.wafflestudio.snutt2.feature.review.components.ReviewStarRating
import com.wafflestudio.snutt2.feature.review.components.ReviewSubmitButton
import com.wafflestudio.snutt2.feature.review.components.ReviewTextInput
import com.wafflestudio.snutt2.ui.components.compose.CenteredTopBar
import com.wafflestudio.snutt2.ui.components.compose.SemesterDropdown
import com.wafflestudio.snutt2.ui.components.compose.SnuttIcon
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.preview.ReviewPreviewData
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.preview.TableSummaryPreviewData
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

data class ReviewWriteRatings(
    val overall: Float = 0f,
    val grade: Float = 0f,
    val teaching: Float = 0f,
    val usefulness: Float = 0f,
    val ease: Float = 0f,
)

@Composable
fun ReviewWriteRoute(onNavigateBack: () -> Unit) {
    ReviewWriteScreen(
        course = null,
        courseBooks = emptyList(),
        selectedCourseBook = null,
        ratings = ReviewWriteRatings(),
        text = "",
        submitEnabled = false,
        onBackClick = onNavigateBack,
        onCourseBookSelected = {},
        onOverallRatingChange = {},
        onGradeRatingChange = {},
        onTeachingRatingChange = {},
        onUsefulnessRatingChange = {},
        onEaseRatingChange = {},
        onTextChange = {},
        onSubmitClick = {},
    )
}

@Composable
fun ReviewWriteScreen(
    course: ReviewedCourse?,
    courseBooks: List<CourseBook>,
    selectedCourseBook: CourseBook?,
    ratings: ReviewWriteRatings,
    text: String,
    submitEnabled: Boolean,
    onBackClick: () -> Unit,
    onCourseBookSelected: (CourseBook) -> Unit,
    onOverallRatingChange: (Float) -> Unit,
    onGradeRatingChange: (Float) -> Unit,
    onTeachingRatingChange: (Float) -> Unit,
    onUsefulnessRatingChange: (Float) -> Unit,
    onEaseRatingChange: (Float) -> Unit,
    onTextChange: (String) -> Unit,
    onSubmitClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(SNUTTColors.BackgroundLight)) {
        CenteredTopBar(
            height = 47.dp,
            title = { Text(stringResource(R.string.review_write_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900)) },
            navigationIcon = {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clicks { onBackClick() }) {
                    SnuttIcon(R.drawable.ic_arrow_back, modifier = Modifier.size(24.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
                    Text(stringResource(R.string.reviews_app_bar_title), style = SNUTTTypography.subtitle1.copy(color = SNUTTColors.Black900))
                }
            },
        )
        Column(modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())) {
            if (course != null) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 24.dp)) {
                    Text(course.title, style = SNUTTTypography.h2.copy(fontSize = 17.sp))
                    Spacer(Modifier.height(4.dp))
                    Text(course.instructor, style = SNUTTTypography.body1.copy(color = SNUTTColors.TextAlternative))
                }
            }
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).background(SNUTTColors.White900, RoundedCornerShape(12.dp))) {
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Spacer(Modifier.height(28.dp))
                    Text(
                        stringResource(R.string.review_select_semester),
                        style = SNUTTTypography.body1.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SNUTTColors.Black900,
                        ),
                    )
                    Spacer(Modifier.height(12.dp))
                    SemesterDropdown(courseBooks, selectedCourseBook, onCourseBookSelected)
                    Spacer(Modifier.height(32.dp))
                    Text(
                        stringResource(R.string.review_overall_title),
                        style = SNUTTTypography.body1.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SNUTTColors.Black900,
                        ),
                    )
                    Spacer(Modifier.height(16.dp))
                    ReviewRatingQuestion(stringResource(R.string.review_metric_overall), ratings.overall, onOverallRatingChange, starSize = 32)
                    Spacer(Modifier.height(24.dp))
                    ReviewRatingQuestion(stringResource(R.string.review_grade_question), ratings.grade, onGradeRatingChange)
                    Spacer(Modifier.height(24.dp))
                    ReviewRatingQuestion(stringResource(R.string.review_teaching_question), ratings.teaching, onTeachingRatingChange)
                    Spacer(Modifier.height(24.dp))
                    ReviewRatingQuestion(stringResource(R.string.review_usefulness_question), ratings.usefulness, onUsefulnessRatingChange)
                    Spacer(Modifier.height(24.dp))
                    ReviewRatingQuestion(stringResource(R.string.review_ease_question), ratings.ease, onEaseRatingChange)
                    Spacer(Modifier.height(28.dp))
                    Divider(thickness = 0.5.dp, color = SNUTTColors.LineLight)
                    Spacer(Modifier.height(28.dp))
                    Text(stringResource(R.string.review_review_title), style = SNUTTTypography.body1.copy(fontSize = 15.sp, color = SNUTTColors.Black900))
                    Spacer(Modifier.height(8.dp))
                    ReviewTextInput(text, onTextChange)
                    Spacer(Modifier.height(24.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
            ReviewSubmitButton(enabled = submitEnabled, onClick = onSubmitClick, modifier = Modifier.align(Alignment.End).padding(end = 20.dp))
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ReviewRatingQuestion(label: String, rating: Float, onRatingChange: (Float) -> Unit, starSize: Int = 28) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, style = SNUTTTypography.body1.copy(fontSize = 15.sp, color = SNUTTColors.TextPlain))
        ReviewStarRating(rating, onRatingChange, starSize = starSize.dp, starSpacing = 4.dp)
    }
}

@SnuttPreview
@Composable
private fun ReviewWriteScreen_Default() {
    SnuttPreviewSurface {
        ReviewWriteScreen(
            course = ReviewPreviewData.course,
            courseBooks = TableSummaryPreviewData.sampleCourseBooks,
            selectedCourseBook = TableSummaryPreviewData.sampleCourseBooks.first(),
            ratings = ReviewWriteRatings(overall = 3f, grade = 2.5f, teaching = 3f, usefulness = 3f, ease = 3f),
            text = "",
            submitEnabled = false,
            onBackClick = {},
            onCourseBookSelected = {},
            onOverallRatingChange = {},
            onGradeRatingChange = {},
            onTeachingRatingChange = {},
            onUsefulnessRatingChange = {},
            onEaseRatingChange = {},
            onTextChange = {},
            onSubmitClick = {},
        )
    }
}
