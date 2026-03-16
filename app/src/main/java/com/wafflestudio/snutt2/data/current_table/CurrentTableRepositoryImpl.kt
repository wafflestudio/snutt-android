package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LocalLecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.domainmodel.Table
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.Unknown
import com.wafflestudio.snutt2.lib.network.dto.PostBookmarkParams
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureReviewDto
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

    override val currentTable: StateFlow<TableDto?> = storage.lastViewedTable.asStateFlow()
        .unwrap(externalScope)

    override suspend fun addLecture(lectureId: String, isForced: Boolean) {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot add lecture when current table not exists")
        val response = api._postAddLecture(prevTable.id, lectureId, PostLectureParams(isForced))
        storage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun removeLecture(lectureId: String) {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot remove lecture when current table not exists")
        val response = api._deleteLecture(prevTable.id, lectureId)
        storage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun createCustomLecture(lecture: PostCustomLectureParams) {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot create custom lecture when current table not exists")
        val response = api._postCustomLecture(prevTable.id, lecture)
        storage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun resetLecture(lectureId: String): LectureDto {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot reset lecture when current table not exists")
        val response = api._resetLecture(prevTable.id, lectureId)
        storage.lastViewedTable.update(response.toOptional())
        return response.lectureList.find { it.id == lectureId }!!
    }

    override suspend fun updateLecture(lectureId: String, target: PutLectureParams) {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot update lecture when current table not exists")
        val response = api._putLecture(prevTable.id, lectureId, target)
        storage.lastViewedTable.update(response.toOptional())
    }

    override suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String,
    ): String {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot update lecture when current table not exists")
        return api._getCoursebooksOfficial(
            prevTable.year,
            prevTable.semester,
            courseNumber,
            lectureNumber,
        ).url
    }

    override suspend fun getBookmarks(): List<LectureDto> {
        return currentTable.value?.let {
            api._getBookmarkList(it.year, it.semester).lectures
        } ?: emptyList()
    }

    override suspend fun getBookmarksOfSemester(year: Long, semester: Long): List<LectureDto> {
        return api._getBookmarkList(year, semester).lectures
    }

    // 유저 시간표 내의 강의는 id가 바뀌어 저장되기 때문에, parent id인 lecture_id 필드를 사용한다.
    // 검색 결과 혹은 관심강좌의 강의는 원본 그대로이므로 lecture_id == null이며, id 필드를 그대로 사용한다.
    override suspend fun addBookmark(lecture: LectureDto): Result<Unit> {
        try {
            val response = api._addBookmark(PostBookmarkParams(lecture.lecture_id ?: lecture.id))
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteBookmark(lecture: LectureDto): Result<Unit> {
        try {
            val response = api._deleteBookmark(PostBookmarkParams(lecture.lecture_id ?: lecture.id))
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getLectureReviewSummary(lectureId: String): LectureReviewDto {
        return api._getLectureReviewSummary(lectureId)
    }

    override suspend fun updateCurrentTable() {
        val prevTable = storage.lastViewedTable.get().value
            ?: return
        val response = api._getTableById(prevTable.id)
        storage.lastViewedTable.update(response.toOptional())
    }

    // 여기부터 리팩토링 코드
    override val currentTableRefactored: StateFlow<Table?>
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

    override suspend fun visitSessionlessLectureList() {
        storage.isVisitedSessionlessLectureList.update(true)
    }

    override suspend fun getBookmarksNew(): Result<List<SearchedLecture>> {
        try {
            val table = currentTable.value
                ?: return Result.Success(listOf())
            return Result.Success(
                table.let {
                    api._getBookmarkList(it.year, it.semester).lectures
                }.map { lecture ->
                    lecture.toSearchedLecture()
                },
            )
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun addBookmarkNew(lecture: Lecture): Result<Unit> {
        try {
            val response = api._addBookmark(PostBookmarkParams(lecture.id))
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteBookmark(lecture: Lecture): Result<Unit> {
        try {
            val response = api._deleteBookmark(PostBookmarkParams(lecture.id))
            return Result.Success(response)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun addLectureNew(lectureId: String, isForced: Boolean): Result<Unit> {
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

    override suspend fun removeLectureNew(lectureId: String): Result<Unit> {
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

    override suspend fun removeLectureNewNew(lecture: SearchedLecture): Result<Unit> {
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

    override suspend fun updateLectureNew(lecture: Lecture, isForced: Boolean): Result<Unit> {
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

    override suspend fun resetLectureNew(lectureId: String): Result<LocalLecture> {
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

    override suspend fun getSyllabusUrlNew(courseNumber: String, lectureNumber: String): Result<String> {
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

    override suspend fun getReviewInfoNew(lectureId: String): Result<LectureReviewInfo?> {
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
