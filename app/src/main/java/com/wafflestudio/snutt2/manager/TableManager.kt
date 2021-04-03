package com.wafflestudio.snutt2.manager

import android.util.Log
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Coursebook
import com.wafflestudio.snutt2.model.Table
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
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

    fun getTableList(callback: Callback<List<Table>>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.getTableList(
            token,
            object : Callback<List<Table>> {
                override fun success(table_list: List<Table>, response: Response) {
                    Log.d(TAG, "get table list request success!")
                    tables!!.clear()
                    tableMap.clear()
                    for (table in table_list) addTable(table)
                    callback.success(tables, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.e(TAG, "get table list request failed..!")
                    callback.failure(error)
                }
            }
        )
    }

    fun postTable(year: Int, semester: Int, title: String?, callback: Callback<List<Table>>?) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["year"] = year
        query["semester"] = semester
        query["title"] = title
        app.restService!!.postTable(
            token,
            query,
            object : Callback<List<Table>> {
                override fun success(table_list: List<Table>, response: Response) {
                    Log.d(TAG, "post new table request success!!")
                    tables!!.clear()
                    tableMap.clear()
                    for (table in table_list) addTable(table)
                    callback?.success(tables, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.e(TAG, "post new table request failed..!")
                    callback?.failure(error)
                }
            }
        )
    }

    fun getTableById(id: String?, callback: Callback<Table>) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.getTableById(
            token,
            id,
            object : Callback<Table> {
                override fun success(table: Table, response: Response) {
                    Log.d(TAG, "get table by id success")
                    LectureManager.instance!!.setLectures(table.lecture_list!!)
                    LectureManager.instance!!.clearSearchedLectures()
                    PrefManager.instance!!.updateNewTable(table)
                    TagManager.instance!!.updateNewTag(table.year, table.semester)
                    callback.success(table, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.d(TAG, "get table by id is failed!")
                    callback.failure(error)
                }
            }
        )
    }

    fun getDefaultTable(callback: Callback<Table>?) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.getRecentTable(
            token,
            object : Callback<Table> {
                override fun success(table: Table, response: Response) {
                    Log.d(TAG, "get recent table request success")
                    LectureManager.instance!!.setLectures(table.lecture_list!!)
                    LectureManager.instance!!.clearSearchedLectures()
                    PrefManager.instance!!.updateNewTable(table)
                    TagManager.instance!!.updateNewTag(table.year, table.semester)
                    callback?.success(table, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.d(TAG, "get recent table request failed!")
                    callback?.failure(error)
                }
            }
        )
    }

    fun getCoursebook(callback: Callback<List<Coursebook>>?) {
        // String token = PrefManager.getInstance().getPrefKeyXAccessToken();
        app.restService!!.getCoursebook(
            object : Callback<List<Coursebook>> {
                override fun success(coursebooks: List<Coursebook>?, response: Response) {
                    Log.d(TAG, "get coursebook request success.")
                    callback?.success(coursebooks, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.d(TAG, "get coursebook request failed.")
                    callback?.failure(error)
                }
            }
        )
    }

    fun deleteTable(id: String?, callback: Callback<List<Table>>?) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        app.restService!!.deleteTable(
            token,
            id,
            object : Callback<List<Table>> {
                override fun success(table_list: List<Table>, response: Response) {
                    Log.d(TAG, "delete table request success.")
                    tables!!.clear()
                    tableMap.clear()
                    for (table in table_list) addTable(table)
                    callback?.success(tables, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.d(TAG, "delete table request failed.")
                    callback?.failure(error)
                }
            }
        )
    }

    fun putTable(id: String?, title: String?, callback: Callback<List<Table>>?) {
        val token: String? = PrefManager.instance!!.prefKeyXAccessToken
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["title"] = title
        app.restService!!.putTable(
            token,
            id,
            query,
            object : Callback<List<Table>> {
                override fun success(table_list: List<Table>, response: Response) {
                    Log.d(TAG, "delete table request success.")
                    tables!!.clear()
                    tableMap.clear()
                    for (table in table_list) addTable(table)
                    callback?.success(tables, response)
                }

                override fun failure(error: RetrofitError) {
                    Log.d(TAG, "delete table request failed.")
                    callback?.failure(error)
                }
            }
        )
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
