package com.wafflestudio.snutt2.views.logged_in.home.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.components.compose.EditText
import com.wafflestudio.snutt2.components.compose.Picker
import com.wafflestudio.snutt2.components.compose.clicks
import com.wafflestudio.snutt2.lib.android.toast
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.toFullString
import com.wafflestudio.snutt2.lib.toFormattedString
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import com.wafflestudio.snutt2.views.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun CreateTableBottomSheet(
    scope: CoroutineScope = rememberCoroutineScope(),
    allCourseBook: List<CourseBookDto>,
    currentCourseBook: CourseBookDto,
    specificSemester: Boolean = false,
    onConfirm: suspend (CourseBookDto, String) -> Unit
) {
    val context = LocalContext.current
    val apiOnProgress = LocalApiOnProgress.current
    val apiOnError = LocalApiOnError.current
    val bottomSheet = LocalBottomSheetState.current
    val drawerState = LocalDrawerState.current
    var newTitle by remember { mutableStateOf("") }
    var pickedCourseBook by remember { mutableStateOf(currentCourseBook) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(25.dp)
            .clicks {}
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "취소", style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    scope.launch {
                        bottomSheet.hide()
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "완료", style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    scope.launch {
                        launchSuspendApi(apiOnProgress, apiOnError) {
                            if (specificSemester) onConfirm(pickedCourseBook, newTitle)
                            else onConfirm(pickedCourseBook, newTitle)
                        }
                        scope.launch {
                            bottomSheet.hide()
                            drawerState.close()
                            context.toast(
                                context.getString(R.string.home_drawer_create_success_message)
                                    .format(pickedCourseBook.toFullString(context), newTitle)
                            )
                        }
                    }
                }
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = "새로운 시간표 만들기",
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Gray600)
        )
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = newTitle,
            onValueChange = { newTitle = it },
            hint = "시간표 제목을 입력하세요",
            underlineColor = if (specificSemester.not()) SNUTTColors.SNUTTTheme else SNUTTColors.Gray200,
            underlineColorFocused = if (specificSemester.not()) SNUTTColors.SNUTTTheme else SNUTTColors.Black900,
            underlineWidth = 2.dp,
        )
        Spacer(modifier = Modifier.height(25.dp))
        if (specificSemester.not()) {
            Spacer(modifier = Modifier.height(5.dp))
            Picker(
                list = allCourseBook,
                initialCenterIndex = allCourseBook.indexOfFirst { it.year == currentCourseBook.year && it.semester == currentCourseBook.semester },
                onValueChanged = { index ->
                    pickedCourseBook = allCourseBook[index]
                },
                PickerItemContent = {
                    Text(
                        text = allCourseBook[it].toFormattedString(context), style = SNUTTTypography.button,
                    )
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
