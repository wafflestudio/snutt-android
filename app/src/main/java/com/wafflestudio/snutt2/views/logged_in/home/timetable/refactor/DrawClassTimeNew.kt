package com.wafflestudio.snutt2.views.logged_in.home.timetable.refactor

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.wafflestudio.snutt2.domainmodel.LectureSession
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.endTimeInFloat
import com.wafflestudio.snutt2.lib.roundToCompact
import com.wafflestudio.snutt2.lib.startTimeInFloat
import com.wafflestudio.snutt2.ui.SNUTTColors
import com.wafflestudio.snutt2.views.logged_in.home.timetable.LectureCellInfo
import com.wafflestudio.snutt2.views.logged_in.home.timetable.calculateAdjustedTextLayout
import kotlin.math.max
import kotlin.math.min

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DrawClassTimeNew(
    session: LectureSession,
    foregroundColor: Int,
    backgroundColor: Int,
    courseTitle: String,
    lectureNumber: String,
    instructorName: String,
    isCustom: Boolean,
    fittedTrimParam: TableTrimParam,
    compactMode: Boolean,
    tableLectureCustomOptions: TableLectureCustom,
) {
    val hourLabelWidth = 24.5.dp
    val dayLabelHeight = 28.5.dp
    val cellPadding = 4.dp
    val textMeasurer = rememberTextMeasurer()

    val dayOffset = session.day.ordinal - fittedTrimParam.dayOfWeekFrom
    val hourRangeOffset =
        Pair(
            max(session.startTimeInFloat - fittedTrimParam.hourFrom, 0f),
            min(
                session.endTimeInFloat.let { if (isCustom.not() && compactMode) roundToCompact(it) else it } - fittedTrimParam.hourFrom,
                fittedTrimParam.hourTo - fittedTrimParam.hourFrom.toFloat() + 1,
            ),
        )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (maxWidth - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (maxHeight - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        Column(
            modifier = Modifier
                .size(
                    width = unitWidth,
                    height = unitHeight * (hourRangeOffset.second - hourRangeOffset.first),
                )
                .offset(
                    x = hourLabelWidth + unitWidth * dayOffset,
                    y = dayLabelHeight + unitHeight * hourRangeOffset.first,
                )
                .border(width = 1.dp, color = SNUTTColors.Black050)
                .background(color = Color(backgroundColor))
                .padding(horizontal = cellPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            BoxWithConstraints {
                val constraints = constraints
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val adjustedTextLayouts = remember(
                        constraints,
                        courseTitle,
                        session.place,
                        lectureNumber,
                        instructorName,
                        fittedTrimParam,
                        tableLectureCustomOptions,
                    ) {
                        try {
                            calculateAdjustedTextLayout(
                                listOf(
                                    LectureCellInfo.titleTextLayout(
                                        courseTitle,
                                        tableLectureCustomOptions.title,
                                    ),
                                    LectureCellInfo.placeTextLayout(
                                        session.place,
                                        tableLectureCustomOptions.place,
                                    ),
                                    LectureCellInfo.lectureNumberTextLayout(
                                        lectureNumber,
                                        tableLectureCustomOptions.lectureNumber,
                                    ),
                                    LectureCellInfo.instructorNameTextLayout(
                                        instructorName,
                                        tableLectureCustomOptions.instructor,
                                    ),
                                ),
                                textMeasurer,
                                constraints,
                            )
                        } catch (e: Exception) {
                            FirebaseCrashlytics.getInstance().recordException(
                                Throwable(
                                    cause = e,
                                    message = "$courseTitle $session $lectureNumber $instructorName $constraints $fittedTrimParam",
                                ),
                            )
                            emptyList()
                        }
                    }

                    adjustedTextLayouts
                        .forEach { textLayout ->
                            if (textLayout.maxLines > 0) {
                                Text(
                                    text = textLayout.text,
                                    style = textLayout.style.copy(color = Color(foregroundColor)),
                                    maxLines = textLayout.maxLines,
                                    textAlign = TextAlign.Center,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            } else {
                                FirebaseCrashlytics.getInstance().recordException(
                                    Throwable(
                                        cause = IllegalStateException(),
                                        message = "$courseTitle $session $lectureNumber $instructorName $constraints $fittedTrimParam",
                                    ),
                                )
                            }
                        }
                }
            }
        }
    }
}
