package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PostTableParams
import com.wafflestudio.snutt2.lib.network.dto.PutTableParams
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap

/**
 * Created by makesource on 2016. 1. 16..
 */
@Singleton
class TableManager @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val lectureManager: LectureManager,
    private val tagManager: TagManager,
    private val prefStorage: PrefStorage
) {
    private var tables: MutableList<TableDto>? = ArrayList()
    private var tableMap: MutableMap<String, TableDto> = HashMap()

    fun reset() {
        tables = ArrayList()
        tableMap = HashMap()
    }

    fun hasTimetables(): Boolean {
        return if (tables == null || tables!!.size == 0) false else true
    }

    fun getTableList(): Single<List<TableDto>> {
        val token: String = prefStorage.prefKeyXAccessToken!!
        return snuttRestApi.getTableList(token)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "get table list request success!")
                tables!!.clear()
                tableMap.clear()
                result.forEach { addTable(it) }
            }
            .doOnError {
                Log.e(TAG, "get table list request failed..!")
            }
    }

    fun postTable(year: Long, semester: Long, title: String?): Single<List<TableDto>> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.postTable(
            token!!,
            PostTableParams(year, semester, title)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post new table request success!!")
                tables!!.clear()
                tableMap.clear()
                it.forEach { addTable(it) }
            }
            .doOnError {
                Log.e(TAG, "post new table request failed..!")
            }
    }

    fun getTableById(id: String?): Single<TableDto> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.getTableById(
            token!!,
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
        val token: String = prefStorage.prefKeyXAccessToken!!
        return snuttRestApi.getRecentTable(token)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get recent table request success")
                lectureManager.setLectures(it.lectureList)
                lectureManager.clearSearchedLectures()
                prefStorage.updateNewTable(it)
                tagManager.updateNewTag(it.year.toInt(), it.semester.toInt())
            }
            .doOnError {
                Log.d(TAG, "get recent table request failed!")
            }
    }

    fun getCoursebook(): Single<List<Coursebook>> {
        // String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        return snuttRestApi.getCoursebook()
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get coursebook request success.")
            }
            .doOnError {
                Log.d(TAG, "get coursebook request failed.")
            }
            .map { it ->
                it.map { item ->
                    Coursebook(item.semester, item.year)
                }
            }
    }

    fun deleteTable(id: String?): Single<List<TableDto>> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.deleteTable(
            token!!,
            id!!
        )
            .doOnSuccess {
                Log.d(TAG, "delete table request success.")
                tables!!.clear()
                tableMap.clear()
                for (table in it) addTable(table)
            }
            .doOnError {
                Log.d(TAG, "delete table request failed.")
            }
    }

    fun putTable(id: String?, title: String?): Single<List<TableDto>> {
        val token: String? = prefStorage.prefKeyXAccessToken
        return snuttRestApi.putTable(
            token!!,
            id!!,
            PutTableParams(title!!)
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "delete table request success.")
                tables!!.clear()
                tableMap.clear()
                for (table in it) addTable(table)
            }
            .doOnError {
                Log.d(TAG, "delete table request failed.")
            }
    }

    fun addTable(table: TableDto) {
        tables!!.add(table)
        tableMap[table.id!!] = table
        Collections.sort(tables)
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
