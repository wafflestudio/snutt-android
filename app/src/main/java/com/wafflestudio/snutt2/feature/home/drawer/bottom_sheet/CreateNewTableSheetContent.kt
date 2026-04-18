package com.wafflestudio.snutt2.feature.home.drawer.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.feature.home.drawer.HomeDrawerBottomSheetType
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.components.compose.Picker
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import com.wafflestudio.snutt2.ui.util.formatter.toFormattedString

@Composable
fun CreateTableBottomSheet(
    sheetState: ModalBottomSheetState,
    sheetType: HomeDrawerBottomSheetType.CreateNewTable,
    onDismiss: () -> Unit,
    onSubmit: (courseBook: CourseBook, newTitle: String) -> Unit,
) {
    val context = LocalContext.current
    var title by remember(sheetType) { mutableStateOf("") }
    var pickedCourseBook by remember { mutableStateOf((sheetType as? HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook)?.initialCourseBook) }

    var clearFocusFlag by remember { mutableStateOf(false) }
    LaunchedEffect(sheetState.isVisible) {
        clearFocusFlag = sheetState.isVisible.not()
    }

    val submit = {
        if (sheetType is HomeDrawerBottomSheetType.CreateNewTable.SpecificCourseBook) {
            onSubmit(sheetType.courseBook, title)
        } else {
            pickedCourseBook?.let {
                onSubmit(it, title)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SNUTTColors.White900)
            .padding(25.dp)
            .clicks {},
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.common_cancel),
                style = SNUTTTypography.body1,
                modifier = Modifier.clicks {
                    onDismiss()
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(R.string.common_complete),
                style = if (title.isNotEmpty()) {
                    SNUTTTypography.body1
                } else {
                    SNUTTTypography.body1.copy(
                        color = SNUTTColors.Gray200,
                    )
                },
                modifier = Modifier.clicks(enabled = title.isNotEmpty()) {
                    submit()
                },
            )
        }
        Spacer(modifier = Modifier.height(25.dp))
        Text(
            text = stringResource(R.string.home_drawer_create_table_bottom_sheet_title),
            style = SNUTTTypography.subtitle2.copy(color = SNUTTColors.Gray600),
        )
        Spacer(modifier = Modifier.height(15.dp))
        EditText(
            value = title,
            onValueChange = { title = it },
            hint = stringResource(R.string.home_drawer_create_table_bottom_sheet_hint),
            underlineColor = SNUTTColors.SNUTTTheme,
            underlineColorFocused = SNUTTColors.SNUTTTheme,
            underlineWidth = 2.dp,
            keyboardActions = KeyboardActions(
                onDone = { submit() },
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            clearFocusFlag = clearFocusFlag,
        )
        Spacer(modifier = Modifier.height(25.dp))
        if (sheetType is HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook) {
            Spacer(modifier = Modifier.height(5.dp))
            Picker(
                list = sheetType.allCourseBook,
                initialCenterIndex = sheetType.allCourseBook.indexOf(sheetType.initialCourseBook),
                onValueChanged = { index ->
                    pickedCourseBook = sheetType.allCourseBook[index]
                },
                PickerItemContent = {
                    Text(
                        text = sheetType.allCourseBook[it].toFormattedString(context),
                        style = SNUTTTypography.button,
                    )
                },
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
private fun CreateTableBottomSheetSelectCourseBookPreview() {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    val sampleCourseBooks = listOf(
        CourseBook(semester = 1, year = 2025),
        CourseBook(semester = 2, year = 2024),
        CourseBook(semester = 1, year = 2024),
    )
    CreateTableBottomSheet(
        sheetState = sheetState,
        sheetType = HomeDrawerBottomSheetType.CreateNewTable.SelectCourseBook(
            initialCourseBook = sampleCourseBooks[0],
            allCourseBook = sampleCourseBooks,
        ),
        onDismiss = {},
        onSubmit = { _, _ -> },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Preview(showBackground = true)
@Composable
private fun CreateTableBottomSheetSpecificCourseBookPreview() {
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Expanded)
    CreateTableBottomSheet(
        sheetState = sheetState,
        sheetType = HomeDrawerBottomSheetType.CreateNewTable.SpecificCourseBook(
            courseBook = CourseBook(semester = 1, year = 2025),
        ),
        onDismiss = {},
        onSubmit = { _, _ -> },
    )
}
