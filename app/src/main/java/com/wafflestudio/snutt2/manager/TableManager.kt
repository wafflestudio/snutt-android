package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Created by makesource on 2016. 1. 16..
 */
@Singleton
class TableManager @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val lectureManager: LectureManager,
    private val tagManager: TagManager,
    private val prefStorage: PrefStorage,
    private val storage: SNUTTStorage
) {
    private var tableMap: MutableMap<String, TableDto> = HashMap()

    fun reset() {
        tableMap = HashMap()
    }

    fun hasTimetables(): Boolean {
        return false
    }

    fun getTableList(): Single<List<TableDto>> {
        return snuttRestApi.getTableList()
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                tableMap.clear()
                storage.tables.setValue(result)
                result.forEach { addTable(it) }
            }
            .doOnError {
                Log.e(TAG, "get table list request failed..!")
            }
    }

    fun postTable(year: Long, semester: Long, title: String?): Single<List<TableDto>> {
        return snuttRestApi.postTable(
            PostTableParams(year, semester, title)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                tableMap.clear()
                it.forEach { addTable(it) }
            }
            .doOnError {
                Log.e(TAG, "post new table request failed..!")
            }
    }

    fun getTableById(id: String?): Single<TableDto> {
        return snuttRestApi.getTableById(
            id!!
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get table by id success")
                lectureManager.setLectures(it.lectureList)
                lectureManager.clearSearchedLectures()
                prefStorage.updateNewTable(it)
                tagManager.updateNewTag(it.year.toInt(), it.semester.toInt())
            }
            .doOnError {
                Log.d(TAG, "get table by id is failed!")
            }
    }

    fun getDefaultTable(): Single<TableDto> {
        return snuttRestApi.getRecentTable()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get recent table request success")
                lectureManager.setLectures(it.lectureList)
                lectureManager.clearSearchedLectures()
                prefStorage.updateNewTable(it)
                tagManager.updateNewTag(it.year.toInt(), it.semester.toInt())
            }
    }

    fun getCoursebook(): Single<List<CourseBookDto>> {
        // String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        return snuttRestApi.getCoursebook()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.courseBooks.setValue(it)
            }
    }

    fun deleteTable(id: String?): Single<List<TableDto>> {
        return snuttRestApi.deleteTable(
            id!!
        )
            .doOnSuccess {
                Log.d(TAG, "delete table request success.")
                tableMap.clear()
                for (table in it) addTable(table)
            }
            .doOnError {
                Log.d(TAG, "delete table request failed.")
            }
    }

    fun putTable(id: String?, title: String?): Single<List<TableDto>> {
        return snuttRestApi.putTable(
            id!!,
            PutTableParams(title!!)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete table request success.")
                tableMap.clear()
                for (table in it) addTable(table)
            }
            .doOnError {
                Log.d(TAG, "delete table request failed.")
            }
    }

    fun addTable(table: TableDto) {
        tableMap[table.id] = table
    }

    fun getTableTitleById(id: String): String? {
        val table = tableMap[id]
        if (table == null) {
            Log.e(TAG, "invalid table id..")
            return null
        }
        return table.title
    }

    fun updateTables(table: TableDto?) {
        // TODO : (SeongWon) server에 update 요청 날리기
    }

    companion object {
        private const val TAG = "TABLE_MANAGER"
    }
}
