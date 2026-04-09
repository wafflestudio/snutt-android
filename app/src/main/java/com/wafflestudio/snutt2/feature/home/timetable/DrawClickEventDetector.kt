package com.wafflestudio.snutt2.feature.home.timetable

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInteropFilter
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.ui.util.contains

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DrawClickEventDetector(
    lectures: List<LocalLecture>,
    fittedTrimParam: TableTrimParam,
    onLectureClick: (LocalLecture) -> Unit,
) {
    var canvasSize by remember { mutableStateOf(Size.Zero) }
    val hourLabelWidth = TimetableCanvasObjects.hourLabelWidth
    val dayLabelHeight = TimetableCanvasObjects.dayLabelHeight
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInteropFilter { event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val unitWidth =
                        (canvasSize.width - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
                    val unitHeight =
                        (canvasSize.height - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

                    val day =
                        ((event.x - hourLabelWidth) / unitWidth).toInt() + fittedTrimParam.dayOfWeekFrom
                    val time =
                        ((event.y - dayLabelHeight) / unitHeight) + fittedTrimParam.hourFrom

                    for (lecture in lectures) {
                        if (lecture.contains(day, time)) {
                            onLectureClick(lecture)
                            break
                        }
                    }
                }
                true
            },
    ) {
        canvasSize = size
    }
}
