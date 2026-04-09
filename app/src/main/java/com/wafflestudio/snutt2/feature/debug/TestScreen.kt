package com.wafflestudio.snutt2.feature.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wafflestudio.snutt2.ui.components.compose.RightArrowIcon
import com.wafflestudio.snutt2.ui.components.compose.SimpleTopBar
import com.wafflestudio.snutt2.ui.components.compose.clicks
import com.wafflestudio.snutt2.ui.util.toast
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.SNUTTTypography

@Composable
fun TestRoute(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit,
    onNavigateOnboard: () -> Unit,
    viewModel: TestViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val uiState by viewModel.testUiState.collectAsStateWithLifecycle()
    val segmentPickerUiState by viewModel.segmentPickerUiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.testUiEvent.collect { uiEvent ->
            when (uiEvent) {
                is TestUiEvent.ShowToast -> {
                    val message = uiEvent.message
                    if (message.isNotEmpty()) {
                        context.toast(message)
                    }
                }

                is TestUiEvent.NavigateToOnboard -> {
                    onNavigateOnboard()
                }
            }
        }
    }

    TestScreen(
        modifier = modifier,
        uiState = uiState,
        segmentPickerUiState = segmentPickerUiState,
        onClickBack = onNavigateBack,
        onFirstTestCase = viewModel::runApiWithoutToken,
        onSecondTestCase = {}, // 구현하려 했으나 번거로워서 일단 스킵
        onThirdTestCase = viewModel::registerLocal,
        onFourthTestCase = viewModel::getNotificationCount,
        onSegmentPickerUiStateChange = viewModel::changeSegmentPickerUiState,
    )
}

@Composable
fun TestScreen(
    modifier: Modifier = Modifier,
    uiState: TestUiState,
    segmentPickerUiState: String,
    onClickBack: () -> Unit,
    onFirstTestCase: () -> Unit,
    onSecondTestCase: () -> Unit,
    onThirdTestCase: (String, String, String) -> Unit,
    onFourthTestCase: () -> Unit,
    onSegmentPickerUiStateChange: (String) -> Unit,
) {
    val text = when (uiState) {
        is TestUiState.Fail -> "실패"
        is TestUiState.Initial -> "초기 상태"
        is TestUiState.Loading -> "대기 중"
        is TestUiState.Success -> "성공 (${uiState.data})"
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SNUTTColors.SettingBackground),
    ) {
        SimpleTopBar(
            title = "Test",
            onClickNavigateBack = onClickBack,
        )

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(10.dp))

            SettingItemForTest(
                title = "현재 상태: $text",
                hasNextPage = false,
                onClick = {},
            )

            Spacer(Modifier.height(10.dp))

            // 누르면 WrongUserToken에 해당하는 Toast가 뜬 후 Onboard 화면으로 이동해야 한다.
            SettingItemForTest(
                title = "Global Exception - WrongUserToken Test",
                hasNextPage = false,
                onClick = onFirstTestCase,
            )

            Spacer(Modifier.height(10.dp))

            SettingItemForTest(
                title = "Global Exception - NoAdminPrivilege Test",
                hasNextPage = false,
                onClick = onSecondTestCase,
            )

            Spacer(Modifier.height(10.dp))

            // 누르면 DuplicateLocalId에 해당하는 Toast가 떠야 한다.
            SettingItemForTest(
                title = "Local Exception - DuplicateLocalId Test",
                hasNextPage = false,
                onClick = { onThirdTestCase("plgafhd", "testtest1234", "plgafhdtest@snu.ac.kr") },
            )

            Spacer(Modifier.height(10.dp))

            // 누르면 현재 상태에, 알림 개수가 반영되어야 한다.
            SettingItemForTest(
                title = "성공하는 경우 Test",
                hasNextPage = false,
                onClick = onFourthTestCase,
            )

            // 리팩토링 과정에서 필요한 테스트가 있다면 지속적으로 추가
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TestScreenPreview() {
    TestScreen(
        uiState = TestUiState.Fail,
        segmentPickerUiState = "option 1",
        onClickBack = {},
        onFirstTestCase = {},
        onSecondTestCase = {},
        onThirdTestCase = { _, _, _ ->
        },
        onFourthTestCase = {},
        onSegmentPickerUiStateChange = { _ -> },
    )
}

@Composable
fun SettingItemForTest(
    title: String,
    modifier: Modifier = Modifier,
    titleColor: Color = MaterialTheme.colors.onSurface,
    leadingIcon: @Composable () -> Unit = {},
    hasNextPage: Boolean = true,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(45.dp)
            .background(MaterialTheme.colors.surface)
            .clicks { if (onClick != null) onClick() }
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingIcon()
        Text(
            text = title,
            style = SNUTTTypography.body1.copy(
                color = titleColor,
            ),
        )
        Spacer(modifier = Modifier.weight(1f))
        content()
        if (hasNextPage) {
            RightArrowIcon(
                modifier = Modifier.size(22.dp),
                colorFilter = ColorFilter.tint(SNUTTColors.Black500),
            )
        }
    }
}
