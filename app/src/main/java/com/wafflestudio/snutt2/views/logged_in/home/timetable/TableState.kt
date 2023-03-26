package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Stable
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam

@Stable
data class TableState(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val previewTheme: TimetableColorTheme?,
)
