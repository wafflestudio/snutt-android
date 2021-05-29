package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.model.Table
import com.wafflestudio.snutt2.network.dto.PostTableParams
import com.wafflestudio.snutt2.network.dto.PutTableParams
import com.wafflestudio.snutt2.network.dto.core.TempUtil
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*

/**
 * Created by makesource on 2016. 1. 16..
 */
class TableManager private constructor(private val app: SNUTTApplication) {
    private var tables: MutableList<Table>?
    private var tableMap: MutableMap<String, Table>
    fun reset() {
        tables = ArrayList()
        tableMap = HashMap()
    }

    fun hasTimetables(): Boolean {
        return if (tables == null || tables!!.size == 0) false else true
    }

    fun getTableList(): Single<List<Table>> {
        val token: String = PrefManager.instance!!.prefKeyXAccessToken!!
        return app.restService!!.getTableList(token)
            .subscribeOn(Schedulers.io())
            .map { result ->
                result.map {
                    Table(it._id, it.title)
                }
            }
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

    fun postTable(year: Long, semester: Long, title: String?): Single<List<Table>> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.postTable(
            token!!,
            PostTableParams(year, semester, title)
        )
            .subscribeOn(Schedulers.io())
            .map { result ->
                result.map {
                    Table(it._id, it.title)
                }
            }
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

    fun getTableById(id: String?): Single<Table> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.getTableById(
            token!!,
            id!!
        )
            .map { TempUtil.toLegacyModel(it) }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get table by id success")
                LectureManager.instance!!.setLectures(it.lecture_list!!)
                LectureManager.instance!!.clearSearchedLectures()
                PrefManager.instance!!.updateNewTable(it)
                TagManager.instance!!.updateNewTag(it.year, it.semester)
            }
            .doOnError {
                Log.d(TAG, "get table by id is failed!")
            }
    }

    fun getDefaultTable(): Single<Table> {
        val token: String = PrefManager.instance!!.prefKeyXAccessToken!!
        return app.restService!!.getRecentTable(token)
            .subscribeOn(Schedulers.io())
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess {
                Log.d(TAG, "get recent table request success")
                LectureManager.instance!!.setLectures(it.lecture_list!!)
                LectureManager.instance!!.clearSearchedLectures()
                PrefManager.instance!!.updateNewTable(it)
                TagManager.instance!!.updateNewTag(it.year, it.semester)
            }
            .doOnError {
                Log.d(TAG, "get recent table request failed!")
            }
    }

    fun getCoursebook(): Single<List<Coursebook>> {
        // String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        return app.restService!!.getCoursebook()
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

    fun deleteTable(id: String?): Single<List<Table>> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.deleteTable(
            token!!,
            id!!
        )
            .map { it.map { TempUtil.toLegacyModel(it) } }
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

    fun putTable(id: String?, title: String?): Single<List<Table>> {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        return app.restService!!.putTable(
            token!!,
            id!!,
            PutTableParams(title!!)
        )
            .map { it.map { TempUtil.toLegacyModel(it) } }
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

    fun addTable(table: Table) {
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

    fun updateTables(table: Table?) {
        // TODO : (SeongWon) server에 update 요청 날리기
    }

    companion object {
        private const val TAG = "TABLE_MANAGER"
        private var singleton: TableManager? = null
        fun getInstance(app: SNUTTApplication): TableManager? {
            if (singleton == null) {
                singleton = TableManager(app)
            }
            return singleton
        }

        @JvmStatic
        val instance: TableManager?
            get() {
                if (singleton == null) Log.e(TAG, "This method should not be called at this time!!")
                return singleton
            }
    }

    /**
     * TableManager 싱글톤
     */
    init {
        tables = ArrayList()
        tableMap = HashMap()
    }
}
