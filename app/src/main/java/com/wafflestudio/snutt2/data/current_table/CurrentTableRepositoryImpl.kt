package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.CustomLecture
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.Unknown
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.lib.unwrap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentTableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
    externalScope: CoroutineScope,
) : CurrentTableRepository {

    override val isVisitedSessionlessLectureList: StateFlow<Boolean> = storage.isVisitedSessionlessLectureList.asStateFlow()

    private val currentTableDto: StateFlow<TableDto?> = storage.lastViewedTable.asStateFlow()
        .unwrap(externalScope)

    override val currentTable: StateFlow<Table?>
        get() = object : StateFlow<Table?> {
            private val source = storage.lastViewedTable.asStateFlow()

            override val value: Table?
                get() = source.value.value?.let { Table.fromTableDto(it) }

            override val replayCache: List<Table?>
                get() = listOf(value)

            override suspend fun collect(collector: kotlinx.coroutines.flow.FlowCollector<Table?>): Nothing {
                source.collect { optionalDto ->
                    collector.emit(optionalDto.value?.let { Table.fromTableDto(it) })
                }
            }
        }

    override suspend fun updateCurrentTable() {
        val prevTable = storage.lastViewedTable.get().value
            ?: return
        val response = api._getTableById(prevTable.id)
        storage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun visitSessionlessLectureList() {
        storage.isVisitedSessionlessLectureList.update(true)
    }

    override suspend fun addLecture(lectureId: String, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val response = api._postAddLecture(prevTable.id, lectureId, PostLectureParams(isForced))
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeLecture(lectureId: String): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val response = api._deleteLecture(prevTable.id, lectureId)
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeLecture(lecture: SearchedLecture): Result<Unit> {
        val table = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        val lectureId = table.lectureList
            .find { it.lecture_id == lecture.id }
            ?.id ?: return Result.Success(Unit)

        return try {
            val response = api._deleteLecture(table.id, lectureId)
            storage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val params = LectureDto.fromLecture(lecture).toParams()
            params.isForced = isForced
            val response = api._putLecture(prevTable.id, lecture.id, params)
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun resetLecture(lectureId: String): Result<LocalLecture> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Fail(Unknown("", ""))
        try {
            val response = api._resetLecture(prevTable.id, lectureId)
            storage.lastViewedTable.update(response.toOptional())
            val lecture = response.lectureList.find { it.id == lectureId }!!.toLocalLecture()
            return Result.Success(lecture)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getSyllabusUrl(courseNumber: String, lectureNumber: String): Result<String> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Fail(Unknown("", ""))
        try {
            val url = api._getCoursebooksOfficial(
                prevTable.year,
                prevTable.semester,
                courseNumber,
                lectureNumber,
            ).url
            return Result.Success(url)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Fail(Unknown("", ""))
        return try {
            val params = LectureDto.fromLocalLecture(lecture).toParams().also { it.isForced = isForced }
            val response = api._postCustomLecture(prevTable.id, params)
            storage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?> {
        try {
            val dto = api._getLectureReviewSummary(lectureId)
            val reviewInfo = LectureReviewInfo(id = dto.id, rating = dto.rating, reviewCount = dto.reviewCount ?: 0)
            return Result.Success(reviewInfo)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    // NOTE: 서버 API에 보낼 강의 ID 필드를 결정한다.
    // 이 ID 선택 로직은 서버 스펙에 종속된 관심사이므로 data layer(repository)에서 처리한다.
    // VacancyRepositoryImpl 에도 동일 로직 존재. 필요 시 공유 유틸로 추출할 것.
    private fun Lecture.resolveApiId(): String = when (this) {
        is SyllabusLecture -> originalLectureId
        else -> id
    }

    private fun LectureDto.toParams() = PostCustomLectureParams(
        id = id,
        course_title = course_title,
        instructor = instructor,
        colorIndex = colorIndex,
        color = color,
        department = department,
        academic_year = academic_year,
        credit = credit,
        classification = classification,
        category = category,
        categoryPre2025 = categoryPre2025,
        remark = remark,
        class_time_json = class_time_json,
    )
}
