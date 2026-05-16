package com.wafflestudio.snutt2.data.tabledisplay

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.TableLectureCustom
import com.wafflestudio.snutt2.domain.model.TableTrimParam
import com.wafflestudio.snutt2.domain.model.toDataModel
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.SNUTTStorage
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
            storage.tableTrimParam.update { prev ->
                TableTrimParam(
                    dayOfWeekFrom = prev.dayOfWeekFrom,
                    dayOfWeekTo = prev.dayOfWeekTo,
                    hourFrom = prev.hourFrom,
                    hourTo = prev.hourTo,
                    forceFitLectures = prev.forceFitLectures.not(),
                ).toDataModel()
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setDayOfWeekRange(from: Int, to: Int): Result<Unit> {
        try {
            storage.tableTrimParam.update { prev ->
                TableTrimParam(
                    dayOfWeekFrom = from,
                    dayOfWeekTo = to,
                    hourFrom = prev.hourFrom,
                    hourTo = prev.hourTo,
                    forceFitLectures = prev.forceFitLectures,
                ).toDataModel()
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun setHourRange(from: Int, to: Int): Result<Unit> {
        try {
            storage.tableTrimParam.update { prev ->
                TableTrimParam(
                    dayOfWeekFrom = prev.dayOfWeekFrom,
                    dayOfWeekTo = prev.dayOfWeekTo,
                    hourFrom = from,
                    hourTo = to,
                    forceFitLectures = prev.forceFitLectures,
                ).toDataModel()
            }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleCompactMode(): Result<Unit> {
        try {
            storage.compactMode.update { it.not() }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleTitleVisible(): Result<Unit> {
        try {
            storage.tableLectureCustom.update { it.copy(title = it.title.not()) }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun togglePlaceVisible(): Result<Unit> {
        try {
            storage.tableLectureCustom.update { it.copy(place = it.place.not()) }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleLectureNumberVisible(): Result<Unit> {
        try {
            storage.tableLectureCustom.update { it.copy(lectureNumber = it.lectureNumber.not()) }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun toggleInstructorVisible(): Result<Unit> {
        try {
            storage.tableLectureCustom.update { it.copy(instructor = it.instructor.not()) }
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
