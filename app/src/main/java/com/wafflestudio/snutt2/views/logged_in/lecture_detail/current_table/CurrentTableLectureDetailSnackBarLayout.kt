package com.wafflestudio.snutt2.views.logged_in.lecture_detail.current_table

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBar
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHost
import com.wafflestudio.snutt2.components.compose.snackbar.CustomSnackBarHostState
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.ui.SNUTTTypography
import dev.chrisbanes.haze.HazeState

@Composable
fun CurrentTableLectureDetailSnackBarLayout(
    snackBarHostState: CustomSnackBarHostState,
    hazeState: HazeState,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState,
                snackBar = { data ->
                    CustomSnackBar(
                        modifier = Modifier.zIndex(10F),
                        snackBarData = snackBarHostState.currentSnackBarData,
                        passedData = data,
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = SNUTTColors.SnackbarBackground,
                        contentStyle = SNUTTTypography.body1.copy(
                            color = SNUTTColors.White,
                            fontWeight = FontWeight.Medium,
                        ),
                        actionLabelStyle = SNUTTTypography.body1.copy(
                            color = SNUTTColors.MilkMint,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        hazeState = hazeState,
                    )
                },
            )
        },
    ) { contentPadding ->
        content(contentPadding)
    }
}
