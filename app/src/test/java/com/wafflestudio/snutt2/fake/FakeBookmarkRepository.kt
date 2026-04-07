package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.bookmark.BookmarkRepository
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.SearchedLecture
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.MutableStateFlow

class FakeBookmarkRepository : BookmarkRepository {

    // --- StateFlow ---
    override val bookmarks = MutableStateFlow<Map<CourseBook, List<SearchedLecture>>>(emptyMap())
    override val firstBookmarkAlert = MutableStateFlow(false)

    // --- 테스트 제어용 필드 ---
    var fetchBookmarksResult: Result<List<SearchedLecture>> = Result.Success(emptyList())

    var addBookmarkResult: Result<Unit> = Result.Success(Unit)
    var addBookmarkCalledWith: Pair<CourseBook, Lecture>? = null
        private set

    var deleteBookmarkResult: Result<Unit> = Result.Success(Unit)
    var deleteBookmarkCalledWith: Pair<CourseBook, Lecture>? = null
        private set

    var setFirstBookmarkAlertShownCalled = false
        private set

    // --- 인터페이스 구현 ---
    override suspend fun fetchBookmarks(courseBook: CourseBook): Result<List<SearchedLecture>> {
        return fetchBookmarksResult
    }

    override suspend fun addBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit> {
        addBookmarkCalledWith = courseBook to lecture
        return addBookmarkResult
    }

    override suspend fun deleteBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit> {
        deleteBookmarkCalledWith = courseBook to lecture
        return deleteBookmarkResult
    }

    override fun setFirstBookmarkAlertShown() {
        setFirstBookmarkAlertShownCalled = true
    }

    // --- 미사용 메서드 ---
    override suspend fun isLectureBookmarked(courseBook: CourseBook, lecture: Lecture): Result<Boolean> =
        TODO("Not used in this test")
}
