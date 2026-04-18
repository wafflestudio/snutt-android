package com.wafflestudio.snutt2.feature.home.timetable

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.getFittingTrimParam
import com.wafflestudio.snutt2.ui.theme.SNUTTTheme

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "SNUTT Light")
@Composable
private fun TimeTableSNUTTLightPreview() {
    SNUTTTheme {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "SNUTT Dark")
@Composable
private fun TimeTableSNUTTDarkPreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = builtInOnlyLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.SNUTT,
                isDarkMode = true,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "Cherry Dark")
@Composable
private fun TimeTableCherryDarkPreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = builtInOnlyLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.CHERRY,
                isDarkMode = true,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "Custom Theme")
@Composable
private fun TimeTableCustomThemePreview() {
    SNUTTTheme {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "BuiltIn + Custom Colors Mixed")
@Composable
private fun TimeTableMixedColorsPreview() {
    SNUTTTheme {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "Custom Colors on Ice Theme")
@Composable
private fun TimeTableMixedOnIcePreview() {
    SNUTTTheme {
        val trimParam = mixedColorLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = mixedColorLectures,
                selectedLecture = null,
                fittedTrimParam = trimParam,
                theme = BuiltInTheme.ICE,
                isDarkMode = false,
                compactMode = false,
                tableLectureCustomOptions = TableLectureCustom.Default,
                touchEnabled = false,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "With Selected Lecture")
@Composable
private fun TimeTableWithSelectedPreview() {
    SNUTTTheme {
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

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "Custom Theme + Custom Colors + Selected")
@Composable
private fun TimeTableCustomThemeMixedWithSelectedPreview() {
    SNUTTTheme {
        val allLectures = mixedColorLectures + listOf(sampleSelectedLecture)
        val trimParam = allLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTable(
                lectures = mixedColorLectures,
                selectedLecture = sampleSelectedLecture,
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
