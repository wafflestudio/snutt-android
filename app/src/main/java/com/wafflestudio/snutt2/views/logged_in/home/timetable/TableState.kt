package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.compose.runtime.Stable
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTheme

@Stable
data class TableState(
    val table: TableDto,
    val trimParam: TableTrimParam,
    val tableLectureCustomOptions: TableLectureCustom,
    val previewTheme: TableTheme?,
) {
    companion object {
        val Default = TableState(
            TableDto.Default,
            TableTrimParam.Default,
            TableLectureCustom.Default,
            null,
        )
    }
}
