package com.wafflestudio.snutt2.feature.themeconfig

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.preview.PreviewData
import com.wafflestudio.snutt2.logging.AnalyticsScreen
import com.wafflestudio.snutt2.logging.compose.logImpression
import com.wafflestudio.snutt2.ui.components.compose.ModalBottomSheetPlaceholder
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors

@Composable
internal fun ThemeConfigBottomSheetLayout(
    sheetState: ModalBottomSheetState,
    bottomSheetType: ThemeConfigUiState.BottomSheetType,
    onClickDetail: (TableTheme) -> Unit,
    onClickApply: (TableTheme) -> Unit,
    onClickDuplicate: (CustomTheme) -> Unit,
    onClickDelete: (CustomTheme) -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            ThemeConfigBottomSheetContent(
                bottomSheetType = bottomSheetType,
                onClickDetail = onClickDetail,
                onClickApply = onClickApply,
                onClickDuplicate = onClickDuplicate,
                onClickDelete = onClickDelete,
            )
        },
        sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
        scrimColor = SNUTTColors.Black.copy(alpha = 0.32f),
        modifier = Modifier.logImpression(AnalyticsScreen.ThemeHome),
    ) {
        content()
    }
}

@Composable
private fun ThemeConfigBottomSheetContent(
    bottomSheetType: ThemeConfigUiState.BottomSheetType,
    onClickDetail: (TableTheme) -> Unit,
    onClickApply: (TableTheme) -> Unit,
    onClickDuplicate: (CustomTheme) -> Unit,
    onClickDelete: (CustomTheme) -> Unit,
) {
    when (bottomSheetType) {
        is ThemeConfigUiState.BottomSheetType.MyCustomThemeActions -> {
            MyCustomThemeMoreActionBottomSheet(
                onClickDetail = { onClickDetail(bottomSheetType.theme) },
                onClickApply = { onClickApply(bottomSheetType.theme) },
                onClickDuplicate = { onClickDuplicate(bottomSheetType.theme) },
                onClickDelete = { onClickDelete(bottomSheetType.theme) },
            )
        }

        is ThemeConfigUiState.BottomSheetType.MarketCustomThemeActions -> {
            MarketCustomThemeMoreActionBottomSheet(
                onClickDetail = { onClickDetail(bottomSheetType.theme) },
                onClickApply = { onClickApply(bottomSheetType.theme) },
                onClickDelete = { onClickDelete(bottomSheetType.theme) },
            )
        }

        is ThemeConfigUiState.BottomSheetType.BuiltInThemeActions -> {
            BuiltInThemeClickBottomSheet(
                onClickDetail = { onClickDetail(bottomSheetType.theme) },
                onClickApply = { onClickApply(bottomSheetType.theme) },
            )
        }

        else -> ModalBottomSheetPlaceholder()
    }
}

// region Previews

@SnuttPreview
@Composable
private fun ThemeConfigBottomSheetContent_MyCustomTheme() {
    SnuttPreviewSurface {
        ThemeConfigBottomSheetContent(
            bottomSheetType = ThemeConfigUiState.BottomSheetType.MyCustomThemeActions(PreviewData.previewCustomTheme1),
            onClickDetail = {},
            onClickApply = {},
            onClickDuplicate = {},
            onClickDelete = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ThemeConfigBottomSheetContent_MarketCustomTheme() {
    SnuttPreviewSurface {
        ThemeConfigBottomSheetContent(
            bottomSheetType = ThemeConfigUiState.BottomSheetType.MarketCustomThemeActions(PreviewData.previewMarketTheme),
            onClickDetail = {},
            onClickApply = {},
            onClickDuplicate = {},
            onClickDelete = {},
        )
    }
}

@SnuttPreview
@Composable
private fun ThemeConfigBottomSheetContent_BuiltInTheme() {
    SnuttPreviewSurface {
        ThemeConfigBottomSheetContent(
            bottomSheetType = ThemeConfigUiState.BottomSheetType.BuiltInThemeActions(BuiltInTheme.fromCode(0)),
            onClickDetail = {},
            onClickApply = {},
            onClickDuplicate = {},
            onClickDelete = {},
        )
    }
}

// endregion
