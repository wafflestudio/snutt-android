package com.wafflestudio.snutt2.data.table_display

import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.toDataModel
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.model.toDomainModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TableDisplayRepositoryImpl @Inject constructor(
    private val storage: SNUTTStorage,
    externalScope: CoroutineScope,
) : TableDisplayRepository {

    override val tableTrimParam: StateFlow<TableTrimParam> = storage.tableTrimParam.asStateFlow().map(externalScope) {
        it.toDomainModel()
    }

    override val tableLectureCustomOption: StateFlow<TableLectureCustom> =
        storage.tableLectureCustom.asStateFlow().map(externalScope) {
            it.toDomainModel()
        }

    override val compactMode: StateFlow<Boolean> = storage.compactMode.asStateFlow()

    override val isVisitedSessionlessLectureList: StateFlow<Boolean> = storage.isVisitedSessionlessLectureList.asStateFlow()

    override suspend fun visitSessionlessLectureList() {
        storage.isVisitedSessionlessLectureList.update(true)
    }

    override suspend fun toggleForceFit(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = prevTrimParam.dayOfWeekFrom,
                    dayOfWeekTo = prevTrimParam.dayOfWeekTo,
                    hourFrom = prevTrimParam.hourFrom,
                    hourTo = prevTrimParam.hourTo,
                    forceFitLectures = prevTrimParam.forceFitLectures.not(),
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setDayOfWeekRange(from: Int, to: Int): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = from,
                    dayOfWeekTo = to,
                    hourFrom = prevTrimParam.hourFrom,
                    hourTo = prevTrimParam.hourTo,
                    forceFitLectures = prevTrimParam.forceFitLectures,
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setHourRange(from: Int, to: Int): Result<Unit> {
        try {
            val prevTrimParam = storage.tableTrimParam.get()
            storage.tableTrimParam.update(
                TableTrimParam(
                    dayOfWeekFrom = prevTrimParam.dayOfWeekFrom,
                    dayOfWeekTo = prevTrimParam.dayOfWeekTo,
                    hourFrom = from,
                    hourTo = to,
                    forceFitLectures = prevTrimParam.forceFitLectures,
                ).toDataModel(),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleCompactMode(): Result<Unit> {
        try {
            val compactMode = storage.compactMode.get()
            storage.compactMode.update(compactMode.not())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleTitleVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(title = prevTrimParam.title.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun togglePlaceVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(place = prevTrimParam.place.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleLectureNumberVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(lectureNumber = prevTrimParam.lectureNumber.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleInstructorVisible(): Result<Unit> {
        try {
            val prevTrimParam = storage.tableLectureCustom.get()
            storage.tableLectureCustom.update(
                prevTrimParam.copy(instructor = prevTrimParam.instructor.not()),
            )
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
