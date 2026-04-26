package com.wafflestudio.snutt2.feature.home.timetable

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTheme
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.feature.home.drawer.VacancyBanner
import com.wafflestudio.snutt2.feature.tablelectures.TableLectureItem
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode

@Composable
fun ScrollableTimetableContent(
    modifier: Modifier = Modifier,
    lectures: List<LocalLecture>,
    vacancyNotificationBannerEnabled: Boolean,
    isSessionlessLectureHintVisible: Boolean,
    onVisitSessionlessLectureList: () -> Unit,
    onClickVacancyBanner: () -> Unit,
    onClickLectureCell: (LocalLecture) -> Unit,
    fittedTrimParam: TableTrimParam,
    theme: TableTheme,
    previewTheme: TableTheme? = null,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
) {
    val sessionlessLectures = lectures.filter { it.lectureSessions.isEmpty() }
    val isSessionlessLectureExists = sessionlessLectures.isNotEmpty()

    val scrollState = rememberLazyListState()
    // scrollState가 saveable이라 화면 복귀 시 위치는 복원되므로, lock 상태도 visited와 동기화한다.
    var scrollUnlocked by remember { mutableStateOf(!isSessionlessLectureHintVisible) }

    val isHintVisible = isSessionlessLectureHintVisible && !scrollUnlocked

    // 스크롤 스펙: 최초에는 일정 속도 이상으로 당겨야 올라감,
    // 한 번 올라간 이후에는 속도 관계없이 스크롤 가능
    val connection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                val velocity = available.y
                return if (velocity <= -50 || scrollUnlocked) {
                    scrollUnlocked = true
                    Offset.Zero
                } else {
                    available
                }
            }

            override suspend fun onPreFling(available: Velocity): Velocity = if (available.y > 0) available else Velocity.Zero
        }
    }

    LaunchedEffect(scrollUnlocked) {
        if (scrollUnlocked) onVisitSessionlessLectureList()
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(connection),
            state = scrollState,
            userScrollEnabled = isSessionlessLectureExists,
        ) {
            item {
                Column(modifier = Modifier.fillParentMaxHeight()) {
                    if (vacancyNotificationBannerEnabled) {
                        VacancyBanner(onClick = onClickVacancyBanner)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                    ) {
                        TimeTable(
                            lectures = lectures,
                            selectedLecture = null,
                            fittedTrimParam = fittedTrimParam,
                            theme = theme,
                            previewTheme = previewTheme,
                            isDarkMode = isDarkMode(),
                            compactMode = compactMode,
                            tableLectureCustomOptions = tableLectureCustomOptions,
                            touchEnabled = true,
                            onLectureClick = onClickLectureCell,
                        )
                    }
                }
            }

            item { Divider(thickness = 2.dp) }

            items(sessionlessLectures) { lecture ->
                TableLectureItem(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 7.dp),
                    lecture = lecture,
                    onClickLecture = onClickLectureCell,
                )
                Row(Modifier.padding(vertical = 5.dp)) {
                    Divider(thickness = 1.dp, color = SNUTTColors.Black050)
                }
            }

            if (sessionlessLectures.isNotEmpty()) {
                item { Spacer(modifier = Modifier.size(52.dp)) }
            }
        }

        AnimatedVisibility(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 10.dp),
            visible = isHintVisible,
            exit = fadeOut(
                animationSpec = tween(
                    durationMillis = 400,
                    easing = FastOutSlowInEasing,
                ),
            ),
        ) {
            HomeSessionlessLectureHint()
        }
    }
}

@Preview(name = "1. Light", uiMode = Configuration.UI_MODE_NIGHT_NO, locale = "ko", heightDp = 1800)
@Preview(name = "2. Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, locale = "ko", heightDp = 1800)
@Composable
private fun ScrollableTimetableContent_Default() {
    SnuttPreviewSurface {
        ScrollableTimetableContent(
            modifier = Modifier.fillMaxSize(),
            lectures = builtInOnlyLectures,
            vacancyNotificationBannerEnabled = true,
            isSessionlessLectureHintVisible = true,
            onVisitSessionlessLectureList = {},
            onClickVacancyBanner = {},
            onClickLectureCell = {},

            fittedTrimParam = TableTrimParam.Default,
            theme = BuiltInTheme.SNUTT,
            previewTheme = null,
            compactMode = false,
            tableLectureCustomOptions = TableLectureCustom.Default,
        )
    }
}
