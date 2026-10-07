package com.wafflestudio.snutt2.ui.components.compose

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewCenteredSurface
import com.wafflestudio.snutt2.ui.preview.TableSummaryPreviewData
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toFormattedString

@Composable
fun SemesterDropdown(
    courseBooks: List<CourseBook>,
    selectedCourseBook: CourseBook?,
    onSelectCourseBook: (CourseBook) -> Unit,
) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .border(
                    width = 1.dp,
                    color = SNUTTColors.Gray200,
                    shape = RoundedCornerShape(4.dp),
                )
                .clicks { expanded = true }
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = selectedCourseBook?.toFormattedString(context) ?: "",
                style = SNUTTTypography.body2.copy(color = SNUTTColors.Black900),
            )
            Spacer(modifier = Modifier.width(5.dp))
            SnuttIcon(R.drawable.ic_arrow_down, modifier = Modifier.size(15.dp), colorFilter = ColorFilter.tint(SNUTTColors.Black900))
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            courseBooks.forEach { courseBook ->
                DropdownMenuItem(
                    onClick = {
                        onSelectCourseBook(courseBook)
                        expanded = false
                    },
                ) {
                    Text(
                        text = courseBook.toFormattedString(context),
                        style = SNUTTTypography.body2.copy(color = SNUTTColors.Black900),
                    )
                }
            }
        }
    }
}

@SnuttPreview
@Composable
private fun SemesterDropdown_Default() {
    SnuttPreviewCenteredSurface {
        SemesterDropdown(
            courseBooks = TableSummaryPreviewData.sampleCourseBooks,
            selectedCourseBook = TableSummaryPreviewData.sampleCourseBooks.first(),
            onSelectCourseBook = {},
        )
    }
}
