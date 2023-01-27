package com.wafflestudio.snutt2.data.current_table

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostBookmarkParams
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PostLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.lib.unwrap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentTableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : CurrentTableRepository {

    override val currentTable: StateFlow<TableDto?> = storage.lastViewedTable.asStateFlow()
        .unwrap(GlobalScope)

    private val _previewTheme = MutableStateFlow<TimetableColorTheme?>(null)

    override val previewTheme: Flow<TimetableColorTheme?>
        get() = _previewTheme

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

    override suspend fun setPreviewTheme(previewTheme: TimetableColorTheme?) {
        _previewTheme.value = previewTheme
    }

    override suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String
    ): String {
        val prevTable = storage.lastViewedTable.get().value
            ?: throw IllegalStateException("cannot update lecture when current table not exists")
        return api._getCoursebooksOfficial(
            prevTable.year,
            prevTable.semester,
            courseNumber,
            lectureNumber
        ).url
    }

    override suspend fun getBookmarks(): List<LectureDto> {
        return currentTable.value?.let {
            api._getBookmarkList(it.year, it.semester).lectures
        } ?: emptyList()
    }

    override suspend fun addBookmark(lecture: LectureDto) {
        api._addBookmark(PostBookmarkParams(lecture.id))
    }

    override suspend fun deleteBookmark(lecture: LectureDto) {
        api._deleteBookmark(PostBookmarkParams(lecture.id))
    }
}
