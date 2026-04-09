package com.wafflestudio.snutt2.data.bookmark

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import kotlinx.coroutines.flow.StateFlow

interface BookmarkRepository {

    val bookmarks: StateFlow<Map<CourseBook, List<SearchedLecture>>>

    suspend fun fetchBookmarks(courseBook: CourseBook): Result<List<SearchedLecture>>

    suspend fun isLectureBookmarked(courseBook: CourseBook, lecture: Lecture): Result<Boolean>

    suspend fun addBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit>

    suspend fun deleteBookmark(courseBook: CourseBook, lecture: Lecture): Result<Unit>

    val firstBookmarkAlert: StateFlow<Boolean>

    fun setFirstBookmarkAlertShown()
}
