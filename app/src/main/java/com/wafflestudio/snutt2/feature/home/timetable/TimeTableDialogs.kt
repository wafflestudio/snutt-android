package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.Table
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableSummary
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.ThemeReference
import com.wafflestudio.snutt2.ui.components.compose.CustomDialog
import com.wafflestudio.snutt2.ui.components.compose.EditText
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@Composable
fun TimeTableDialogs(
    uiState: TimeTableUiState.Loaded,
    onDismiss: () -> Unit,
    onChangeTableNameTitleChange: (String) -> Unit,
    onConfirmChangeTableTitle: () -> Unit,
) {
    when (uiState.dialogState) {
        TimeTableUiState.DialogState.None -> {}

        // FIXME: HomeDrawerDialogs 랑 중복 코드
        is TimeTableUiState.DialogState.ChangeTableName -> {
            CustomDialog(
                onDismiss = onDismiss,
                onConfirm = onConfirmChangeTableTitle,
                title = stringResource(R.string.home_drawer_change_name_dialog_title),
                positiveButtonText = stringResource(R.string.common_ok),
                negativeButtonText = stringResource(R.string.common_cancel),
            ) {
                EditText(
                    value = uiState.dialogState.newTitle,
                    onValueChange = onChangeTableNameTitleChange,
                )
            }
        }
    }
}

@SnuttPreview
@Composable
private fun TimeTableDialogs_ChangeTableName() {
    SnuttPreviewSurface {
        TimeTableDialogs(
            uiState = TimeTableUiState.Loaded(
                table = Table(
                    summary = TableSummary.Default,
                    lectures = emptyList(),
                    themeRef = ThemeReference.BuiltIn(0),
                ),
                theme = BuiltInTheme.SNUTT,
                previewTheme = null,
                tableTrimParam = TableTrimParam.Default,
                isCompactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                newSemesterExist = false,
                uncheckedNotificationExist = false,
                vacancyNotificationBannerEnabled = false,
                isSessionlessLectureHintVisible = false,
                dialogState = TimeTableUiState.DialogState.ChangeTableName(
                    tableSummary = TableSummary.Default,
                ),
            ),
            onDismiss = {},
            onChangeTableNameTitleChange = {},
            onConfirmChangeTableTitle = {},
        )
    }
}
