package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.table_display.TableDisplayRepository
import com.wafflestudio.snutt2.domainmodel.TableLectureCustom
import com.wafflestudio.snutt2.domainmodel.TableTrimParam
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.MutableStateFlow

class FakeTableDisplayRepository : TableDisplayRepository {

    override val tableTrimParam = MutableStateFlow(TableTrimParam.Default)
    override val tableLectureCustomOption = MutableStateFlow(TableLectureCustom.Default)
    override val compactMode = MutableStateFlow(false)
    override val isVisitedSessionlessLectureList = MutableStateFlow(false)

    // --- 테스트 제어용 필드 ---
    var toggleForceFitResult: Result<Unit> = Result.Success(Unit)
    var toggleForceFitCalled = false
        private set

    var setDayOfWeekRangeResult: Result<Unit> = Result.Success(Unit)
    var setDayOfWeekRangeCalledWith: Pair<Int, Int>? = null
        private set

    var setHourRangeResult: Result<Unit> = Result.Success(Unit)
    var setHourRangeCalledWith: Pair<Int, Int>? = null
        private set

    var toggleCompactModeResult: Result<Unit> = Result.Success(Unit)
    var toggleCompactModeCalled = false
        private set

    var toggleTitleVisibleResult: Result<Unit> = Result.Success(Unit)
    var toggleTitleVisibleCalled = false
        private set

    var togglePlaceVisibleResult: Result<Unit> = Result.Success(Unit)
    var togglePlaceVisibleCalled = false
        private set

    var toggleLectureNumberVisibleResult: Result<Unit> = Result.Success(Unit)
    var toggleLectureNumberVisibleCalled = false
        private set

    var toggleInstructorVisibleResult: Result<Unit> = Result.Success(Unit)
    var toggleInstructorVisibleCalled = false
        private set

    // --- 인터페이스 구현 ---
    override suspend fun toggleForceFit(): Result<Unit> {
        toggleForceFitCalled = true
        return toggleForceFitResult
    }

    override suspend fun setDayOfWeekRange(from: Int, to: Int): Result<Unit> {
        setDayOfWeekRangeCalledWith = from to to
        return setDayOfWeekRangeResult
    }

    override suspend fun setHourRange(from: Int, to: Int): Result<Unit> {
        setHourRangeCalledWith = from to to
        return setHourRangeResult
    }

    override suspend fun toggleCompactMode(): Result<Unit> {
        toggleCompactModeCalled = true
        return toggleCompactModeResult
    }

    override suspend fun toggleTitleVisible(): Result<Unit> {
        toggleTitleVisibleCalled = true
        return toggleTitleVisibleResult
    }

    override suspend fun togglePlaceVisible(): Result<Unit> {
        togglePlaceVisibleCalled = true
        return togglePlaceVisibleResult
    }

    override suspend fun toggleLectureNumberVisible(): Result<Unit> {
        toggleLectureNumberVisibleCalled = true
        return toggleLectureNumberVisibleResult
    }

    override suspend fun toggleInstructorVisible(): Result<Unit> {
        toggleInstructorVisibleCalled = true
        return toggleInstructorVisibleResult
    }

    // --- 미사용 메서드 ---
    override suspend fun visitSessionlessLectureList() = TODO("Not used in this test")
}
