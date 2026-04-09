package com.wafflestudio.snutt2.components.compose.snackbar

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography
import kotlinx.coroutines.launch

@Composable
fun SampleCustomSnackBarWithoutAction() {
    val snackBarHostState = remember { CustomSnackBarHostState() }

    // 예시를 위해 LaunchedEffect를 사용했으나 실제로는 필요에 따라 사용해야 함.
    LaunchedEffect(Unit) {
        launch {
            snackBarHostState.currentSnackBarData.dismiss() // 두 개의 SnackBar가 겹칠 시, 이전 SnackBar를 즉시 dismiss 하기 원하는 경우.
            snackBarHostState.showSnackBar(
                message = "Sample SnackBar Without Action",
                duration = CustomSnackBarDuration(
                    fadeIn = 500L,
                    inBetween = 3000L,
                    fadeOut = 500L,
                ),
            )
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState,
                snackBar = { data ->
                    val currentSnackBarData = snackBarHostState.currentSnackBarData
                    CustomSnackBar(
                        snackBarData = currentSnackBarData,
                        passedData = data, // 이 파라미터가 중요합니다.
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = SNUTTColors.Black500,
                        contentStyle = SNUTTTypography.body1.copy(color = SNUTTColors.White, fontWeight = FontWeight.Medium),
                        actionLabelStyle = SNUTTTypography.body1.copy(color = SNUTTColors.MilkMint, fontWeight = FontWeight.SemiBold),
                    )
                },
            )
        },
    ) { padding ->
        Text(
            text = "Screen Body content",
            modifier = Modifier.padding(padding).fillMaxSize().wrapContentSize(),
        )
    }
}

@Composable
fun SampleCustomSnackBarWithAction() {
    val snackBarHostState = remember { CustomSnackBarHostState() }

    // 예시를 위해 LaunchedEffect를 사용했으나 실제로는 필요에 따라 사용해야 함.
    LaunchedEffect(Unit) {
        launch {
            snackBarHostState.currentSnackBarData.dismiss() // 두 개의 SnackBar가 겹칠 시, 이전 SnackBar를 즉시 dismiss 하기 원하는 경우.
            val result = snackBarHostState.showSnackBar(
                message = "Sample SnackBar Without Action",
                actionLabel = "Action",
                duration = CustomSnackBarDuration(
                    fadeIn = 500L,
                    inBetween = 3000L,
                    fadeOut = 500L,
                ),
            )
            if (result == SnackbarResult.ActionPerformed) {
                // Action Button 클릭 시 동작
            }
        }
    }

    Scaffold(
        snackbarHost = {
            CustomSnackBarHost(
                hostState = snackBarHostState,
                snackBar = { data ->
                    val currentSnackBarData = snackBarHostState.currentSnackBarData
                    CustomSnackBar(
                        snackBarData = currentSnackBarData,
                        passedData = data, // 이 파라미터가 중요합니다.
                        shape = RoundedCornerShape(10.dp),
                        backgroundColor = SNUTTColors.Black500,
                        contentStyle = SNUTTTypography.body1.copy(color = SNUTTColors.White, fontWeight = FontWeight.Medium),
                        actionLabelStyle = SNUTTTypography.body1.copy(color = SNUTTColors.MilkMint, fontWeight = FontWeight.SemiBold),
                    )
                },
            )
        },
    ) { padding ->
        Text(
            text = "Screen Body content",
            modifier = Modifier.padding(padding).fillMaxSize().wrapContentSize(),
        )
    }
}
