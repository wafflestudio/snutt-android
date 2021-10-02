package com.wafflestudio.snutt2.data.current_table

import androidx.datastore.core.DataStore
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.lib.network.dto.PutLectureParams
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.storage.CurrentTablePreferences
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentTableRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val currentTableStore: DataStore<CurrentTablePreferences>
) : CurrentTableRepository {

    override val currentTable: Flow<TableDto> = currentTableStore.data
        .map { it.data }
        .filterNotNull()
        .distinctUntilChanged()

    override suspend fun addLecture(lectureId: String) {
        currentTableStore.updateData { prev ->
            val prevTable = prev.data
                ?: throw IllegalStateException("cannot add lecture when current table not exists")
            val response = api._postAddLecture(prevTable.id, lectureId)
            prev.copy(data = response)
        }
    }

    override suspend fun removeLecture(lectureId: String) {
        currentTableStore.updateData { prev ->
            val prevTable = prev.data
                ?: throw IllegalStateException("cannot remove lecture when current table not exists")
            val response = api._deleteLecture(prevTable.id, lectureId)
            prev.copy(data = response)
        }
    }

    override suspend fun createCustomLecture(lecture: PostCustomLectureParams) {
        currentTableStore.updateData { prev ->
            val prevTable = prev.data
                ?: throw IllegalStateException("cannot create custom lecture when current table not exists")
            val response = api._postCustomLecture(prevTable.id, lecture)
            prev.copy(data = response)
        }
    }

    override suspend fun resetLecture(lectureId: String) {
        currentTableStore.updateData { prev ->
            val prevTable = prev.data
                ?: throw IllegalStateException("cannot reset lecture when current table not exists")
            val response = api._resetLecture(prevTable.id, lectureId)
            prev.copy(data = response)
        }
    }

    override suspend fun updateLecture(lectureId: String, target: PutLectureParams) {
        currentTableStore.updateData { prev ->
            val prevTable = prev.data
                ?: throw IllegalStateException("cannot update lecture when current table not exists")
            val response = api._putLecture(prevTable.id, lectureId, target)
            prev.copy(data = response)
        }
    }

    override suspend fun getLectureSyllabusUrl(
        courseNumber: String,
        lectureNumber: String
    ): String {
        val prevTable = currentTableStore.data.first().data
            ?: throw IllegalStateException("cannot update lecture when current table not exists")
        return api._getCoursebooksOfficial(
            prevTable.year,
            prevTable.semester,
            courseNumber,
            lectureNumber
        ).url
    }
}
