package com.wafflestudio.snutt2.components.compose.snackbar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import dev.chrisbanes.haze.HazeState

@Composable
fun SnackBarScaffold(
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
