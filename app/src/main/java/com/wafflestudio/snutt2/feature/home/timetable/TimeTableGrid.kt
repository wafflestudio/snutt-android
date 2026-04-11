package com.wafflestudio.snutt2.feature.home.timetable

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.ui.theme.SNUTTColors
import com.wafflestudio.snutt2.ui.theme.isDarkMode
import com.wafflestudio.snutt2.ui.util.toDayString

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DrawTableGrid(fittedTrimParam: TableTrimParam) {
    val context = LocalContext.current
    val gridColor = SNUTTColors.TableGrid
    val gridColor2 = SNUTTColors.TableGrid2

    val hourLabelWidth = 24.5.dp
    val dayLabelHeight = 28.5.dp

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val unitWidth =
            (maxWidth - hourLabelWidth) / (fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1)
        val unitHeight =
            (maxHeight - dayLabelHeight) / (fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1)

        val verticalLines = fittedTrimParam.dayOfWeekTo - fittedTrimParam.dayOfWeekFrom + 1
        val horizontalLines = fittedTrimParam.hourTo - fittedTrimParam.hourFrom + 1

        repeat(verticalLines) { idx ->
            Box(
                modifier = Modifier
                    .offset(x = hourLabelWidth + unitWidth * idx)
                    .size(width = 0.5.dp, height = maxHeight)
                    .background(gridColor),
            )
            Box(
                modifier = Modifier
                    .offset(x = hourLabelWidth + unitWidth * idx)
                    .size(width = unitWidth, height = dayLabelHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = (fittedTrimParam.dayOfWeekFrom + idx).toDayString(context),
                    textAlign = TextAlign.Center,
                    color = if (isDarkMode()) {
                        Color(119, 119, 119, 180)
                    } else {
                        Color(0, 0, 0, 180)
                    },
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                )
            }
        }
        repeat(horizontalLines) { idx ->
            Box(
                modifier = Modifier
                    .offset(y = dayLabelHeight + unitHeight * idx)
                    .size(width = maxWidth, height = 0.5.dp)
                    .background(gridColor),
            )
            Box(
                modifier = Modifier
                    .offset(
                        x = hourLabelWidth,
                        y = dayLabelHeight + unitHeight * idx + unitHeight * 0.5f,
                    )
                    .size(width = maxWidth, height = 0.5.dp)
                    .background(gridColor2),
            )
            Box(
                modifier = Modifier
                    .offset(y = dayLabelHeight + unitHeight * idx)
                    .size(width = hourLabelWidth, height = unitHeight)
                    .padding(top = 4.dp, end = 4.dp),
                contentAlignment = Alignment.TopEnd,
            ) {
                Text(
                    text = (fittedTrimParam.hourFrom + idx).toString(),
                    textAlign = TextAlign.Right,
                    color = if (isDarkMode()) {
                        Color(119, 119, 119, 180)
                    } else {
                        Color(0, 0, 0, 180)
                    },
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                )
            }
        }
    }
}
