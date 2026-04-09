package com.wafflestudio.snutt2.data.table_display

import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.StateFlow

interface TableDisplayRepository {
    val tableTrimParam: StateFlow<TableTrimParam>

    val tableLectureCustomOption: StateFlow<TableLectureCustom>

    val compactMode: StateFlow<Boolean>

    val isVisitedSessionlessLectureList: StateFlow<Boolean>

    suspend fun visitSessionlessLectureList()

    suspend fun toggleForceFit(): Result<Unit>

    suspend fun setDayOfWeekRange(from: Int, to: Int): Result<Unit>

    suspend fun setHourRange(from: Int, to: Int): Result<Unit>

    suspend fun toggleCompactMode(): Result<Unit>

    suspend fun toggleTitleVisible(): Result<Unit>

    suspend fun togglePlaceVisible(): Result<Unit>

    suspend fun toggleLectureNumberVisible(): Result<Unit>

    suspend fun toggleInstructorVisible(): Result<Unit>
}
