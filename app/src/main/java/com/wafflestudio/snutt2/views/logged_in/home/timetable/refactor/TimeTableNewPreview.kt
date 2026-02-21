package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.getFittingTrimParam
import com.wafflestudio.snutt2.ui.SNUTTTheme

@Preview(showBackground = true, widthDp = 360, heightDp = 600, name = "SNUTT Light")
@Composable
private fun TimeTableNewSNUTTLightPreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewSNUTTDarkPreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewCherryDarkPreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewCustomThemePreview() {
    SNUTTTheme {
        val trimParam = builtInOnlyLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewMixedColorsPreview() {
    SNUTTTheme {
        val trimParam = mixedColorLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewMixedOnIcePreview() {
    SNUTTTheme {
        val trimParam = mixedColorLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewWithSelectedPreview() {
    SNUTTTheme {
        val allLectures = builtInOnlyLectures + listOf(sampleSelectedLecture)
        val trimParam = allLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
private fun TimeTableNewCustomThemeMixedWithSelectedPreview() {
    SNUTTTheme {
        val allLectures = mixedColorLectures + listOf(sampleSelectedLecture)
        val trimParam = allLectures.getFittingTrimParam(TableTrimParam.Default)
        Box(modifier = Modifier.fillMaxSize()) {
            TimeTableNew(
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
