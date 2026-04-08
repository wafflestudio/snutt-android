package com.wafflestudio.snutt2.data.bookmark

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.mapper.toSearchedLecture
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.PostBookmarkParams
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : BookmarkRepository {

    private val _bookmarks = MutableStateFlow<Map<CourseBook, List<SearchedLecture>>>(emptyMap())
    override val bookmarks: StateFlow<Map<CourseBook, List<SearchedLecture>>> = _bookmarks.asStateFlow()

    override suspend fun fetchBookmarks(courseBook: CourseBook): Result<List<SearchedLecture>> {
        try {
            val list = getOrFetch(courseBook)
            return Result.Success(list)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun isLectureBookmarked(courseBook: CourseBook, lecture: Lecture): Result<Boolean> {
        try {
            val list = getOrFetch(courseBook)
            val targetId = lecture.resolveApiId()
            return Result.Success(list.any { it.id == targetId })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun addBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit> {
        try {
            when (lecture) {
                is SyllabusLecture -> api._addBookmark(PostBookmarkParams(lecture.originalLectureId))
                else -> api._addBookmark(PostBookmarkParams(lecture.id))
            }
            refetch(courseBook)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit> {
        try {
            when (lecture) {
                is SyllabusLecture -> api._deleteBookmark(PostBookmarkParams(lecture.originalLectureId))
                else -> api._deleteBookmark(PostBookmarkParams(lecture.id))
            }
            refetch(courseBook)
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override val firstBookmarkAlert = storage.firstBookmarkAlert.asStateFlow()

    override fun setFirstBookmarkAlertShown() {
        storage.firstBookmarkAlert.update(false)
    }

    private suspend fun getOrFetch(courseBook: CourseBook): List<SearchedLecture> {
        _bookmarks.value[courseBook]?.let { return it }
        val lectures = api._getBookmarkList(courseBook.year, courseBook.semester)
            .lectures
            .map { it.toSearchedLecture() }
        _bookmarks.update { it + (courseBook to lectures) }
        return lectures
    }

    private suspend fun refetch(courseBook: CourseBook) {
        val lectures = api._getBookmarkList(courseBook.year, courseBook.semester)
            .lectures
            .map { it.toSearchedLecture() }
        _bookmarks.update { it + (courseBook to lectures) }
    }

    // NOTE: 서버 API에 보낼 강의 ID 필드를 결정한다.
    // 이 ID 선택 로직은 서버 스펙에 종속된 관심사이므로 data layer(repository)에서 처리한다.
    private fun Lecture.resolveApiId(): String = when (this) {
        is SyllabusLecture -> originalLectureId
        else -> id
    }
}
