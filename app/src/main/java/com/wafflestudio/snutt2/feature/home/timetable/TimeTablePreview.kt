package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.getFittingTrimParam
import com.wafflestudio.snutt2.ui.preview.SnuttPreview
import com.wafflestudio.snutt2.ui.preview.SnuttPreviewSurface

@SnuttPreview
@Composable
private fun TimeTable_BuiltInTheme() {
    SnuttPreviewSurface {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = builtInOnlyLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.SNUTT,
                isDarkMode = false,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@SnuttPreview
@Composable
private fun TimeTable_CustomTheme() {
    SnuttPreviewSurface {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = builtInOnlyLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = sampleCustomTheme,
                isDarkMode = false,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@SnuttPreview
@Composable
private fun TimeTable_MixedColorsBuiltInAndCustom() {
    SnuttPreviewSurface {
        val trimParam = mixedColorLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = mixedColorLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.SNUTT,
                isDarkMode = false,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@SnuttPreview
@Composable
private fun TimeTable_WithSelectedLecture() {
    SnuttPreviewSurface {
        val allLectures = builtInOnlyLectures + listOf(sampleSelectedLecture)
        val trimParam = allLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = builtInOnlyLectures,
                selectedLecture = sampleSelectedLecture,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.SNUTT,
                isDarkMode = false,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}
