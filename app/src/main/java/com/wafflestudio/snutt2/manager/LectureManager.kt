package com.wafflestudio.snutt2.manager

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Color
import com.wafflestudio.snutt2.model.ColorList
import com.wafflestudio.snutt2.model.Lecture
import com.wafflestudio.snutt2.model.Table
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import java.util.*

/**
 * Created by makesource on 2016. 2. 7..
 */
class LectureManager private constructor(private val app: SNUTTApplication) {
    private var lectures: MutableList<Lecture>
    private var selectedLecture: Lecture? = null
    var currentLecture: Lecture? = null
    private var searchedQuery: String? = null

    // Search query
    private var searchedLectures: MutableList<Lecture>

    // color list
    var colorList: List<Color>? = null
        private set
    var colorNameList: List<String>? = null
        private set

    private fun loadColorData() {
        val type1 = object : TypeToken<List<Color?>?>() {}.type
        colorList = Gson().fromJson(PrefManager.instance!!.lectureColors, type1)
        val type2 = object : TypeToken<List<String?>?>() {}.type
        colorNameList = Gson().fromJson(PrefManager.instance!!.lectureColorNames, type2)
        if (colorList == null) {
            colorList = ArrayList()
        }
        if (colorNameList == null) {
            colorNameList = ArrayList()
        }
    }

    interface OnLectureChangedListener {
        fun notifyLecturesChanged()
        fun notifySearchedLecturesChanged()
    }

    private val listeners: MutableList<OnLectureChangedListener> = ArrayList()
    fun addListener(listener: OnLectureChangedListener) {
        for (i in listeners.indices) {
            if (listeners[i] == listener) {
                Log.w(TAG, "listener reference is duplicated !!")
                return
            }
        }
        listeners.add(listener)
    }

    fun removeListener(listener: OnLectureChangedListener) {
        for (i in listeners.indices) {
            val reference = listeners[i]
            if (reference === listener) {
                listeners.removeAt(i)
                break
            }
        }
    }

    ////////
    fun reset() {
        lectures = ArrayList()
        searchedLectures = ArrayList()
        currentLecture = null
        selectedLecture = null
        searchedQuery = null
    }

    fun getLectures(): List<Lecture> {
        return lectures
    }

    fun getSearchedLectures(): List<Lecture> {
        return searchedLectures
    }

    fun setLectures(lecture_list: List<Lecture>) {
        lectures.clear()
        for (lecture in lecture_list) {
            lectures.add(lecture)
        }
        notifyLecturesChanged()
    }

    fun setSearchedLectures(lecture_list: List<Lecture>) {
        searchedLectures.clear()
        for (lecture in lecture_list) {
            searchedLectures.add(lecture)
        }
        notifySearchedLecturesChanged()
    }

    fun getSelectedLecture(): Lecture? {
        return selectedLecture
    }

    fun setSelectedLecture(selectedLecture: Lecture?) {
        this.selectedLecture = selectedLecture
        notifyLecturesChanged()
    }

    val selectedPosition: Int
        get() = if (selectedLecture == null) -1 else searchedLectures.indexOf(selectedLecture)

    // this is for searched lecture
    fun addLecture(lec: Lecture, callback: Callback<Any>) {
        if (alreadyOwned(lec)) {
            Log.w(TAG, "lecture is duplicated!! ")
            return
        }
        if (alreadyExistClassTime(lec)) {
            Log.d(TAG, "lecture is duplicated!! ")
            Toast.makeText(app, "시간표의 시간과 겹칩니다", Toast.LENGTH_SHORT).show()
            return
        }
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        val lectureId = lec.id
        app.restService!!.postLecture(token, id, lectureId, object : Callback<Table> {
            override fun success(table: Table?, response: Response) {
                Log.d(TAG, "post lecture request success!!")
                PrefManager.instance!!.updateNewTable(table!!)
                setLectures(table.lecture_list!!)
                notifyLecturesChanged()
                callback.success(table, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "post lecture request failed ...")
                callback.failure(error)
            }
        })
    }

    // this is for searched lecture
    fun removeLecture(target: Lecture, callback: Callback<Any>) {
        for (lecture in lectures) {
            if (isEqualLecture(target, lecture)) {
                removeLecture(lecture.id, callback)
                return
            }
        }
        Log.w(TAG, "lecture is not exist!!")
    }

    // this is for custom lecture
    fun createLecture(lecture: Lecture?, callback: Callback<Table>?) {
        Log.d(TAG, "create lecture method called!!")
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        app.restService!!.postLecture(token, id, lecture, object : Callback<Table> {
            override fun success(table: Table?, response: Response) {
                PrefManager.instance!!.updateNewTable(table!!)
                setLectures(table.lecture_list!!)
                notifyLecturesChanged()
                callback?.success(table, response)
            }

            override fun failure(error: RetrofitError) {
                callback?.failure(error)
                Log.e(TAG, "post lecture request failed..")
            }
        })
    }

    fun removeLecture(lectureId: String?, callback: Callback<Any>?) {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        app.restService!!.deleteLecture(token, id, lectureId, object : Callback<Table> {
            override fun success(table: Table?, response: Response) {
                Log.d(TAG, "remove lecture request success!!")
                PrefManager.instance!!.updateNewTable(table!!)
                setLectures(table.lecture_list!!)
                currentLecture = null
                notifyLecturesChanged()
                callback?.success(table, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "remove lecture request failed ...")
                callback?.failure(error)
            }
        })
    }

    // reset lecture from my lecture list
    // _id 는 유지된다
    fun resetLecture(lectureId: String, callback: Callback<Any>) {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        app.restService!!.resetLecture(token, id, lectureId, object : Callback<Table> {
            override fun success(table: Table?, response: Response) {
                Log.d(TAG, "reset lecture request success!!")
                PrefManager.instance!!.updateNewTable(table!!)
                setLectures(table.lecture_list!!)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
                callback.success(table, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "reset lecture request failed ...")
                callback.failure(error)
            }
        })
    }

    fun updateLecture(lectureId: String, target: Lecture?, callback: Callback<Table>?) {
        Log.d(TAG, "update lecture method called!!")
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        app.restService!!.putLecture(token, id, lectureId, target, object : Callback<Table> {
            override fun success(table: Table?, response: Response) {
                PrefManager.instance!!.updateNewTable(table!!)
                setLectures(table.lecture_list!!)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
                callback?.success(table, response)
            }

            override fun failure(error: RetrofitError) {
                callback?.failure(error)
                Log.e(TAG, "put lecture request failed..")
            }
        })
    }

    fun getCoursebookUrl(courseNumber: String?, lectureNumber: String?, callback: Callback<Map<*, *>>) {
        val year = PrefManager.instance!!.currentYear
        val semester = PrefManager.instance!!.currentSemester
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["year"] = year
        query["semester"] = semester
        query["course_number"] = courseNumber
        query["lecture_number"] = lectureNumber
        app.restService!!.getCoursebooksOfficial(query, object : Callback<Map<*, *>> {
            override fun success(map: Map<*, *>?, response: Response) {
                Log.d(TAG, "get coursebook official request success!")
                callback.success(map, response)
            }

            override fun failure(error: RetrofitError) {
                Log.e(TAG, "get coursebook official request failed..")
                callback.failure(error)
            }
        })
    }

    //내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    fun alreadyOwned(lec: Lecture): Boolean {
        for (lecture in lectures) {
            if (isEqualLecture(lecture, lec)) return true
        }
        return false
    }

    //이미 내 강의에 존재하는 시간인지
    fun alreadyExistClassTime(lec: Lecture): Boolean {
        for (lecture in lectures) {
            if (isDuplicatedClassTime(lecture, lec)) return true
        }
        return false
    }

    //주어진 요일, 시각을 포함하고 있는지
    fun contains(lec1: Lecture, given_day: Int, given_time: Float): Boolean {
        for (element1 in lec1.class_time_json!!) {
            val class1 = element1.asJsonObject
            val day1 = class1["day"].asInt
            val start1 = class1["start"].asFloat
            val len1 = class1["len"].asFloat
            val end1 = start1 + len1
            val len2 = 0.5f
            val end2 = given_time + len2
            if (day1 != given_day) continue
            if (!(end1 <= given_time || end2 <= start1)) return true
        }
        return false
    }

    fun postSearchQuery(text: String?, callback: Callback<List<Lecture>>) {
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["year"] = PrefManager.instance!!.currentYear
        query["semester"] = PrefManager.instance!!.currentSemester
        query["title"] = text
        query["classification"] = TagManager.instance!!.getClassification()
        query["credit"] = TagManager.instance!!.getCredit()
        query["academic_year"] = TagManager.instance!!.getAcademic_year()
        query["instructor"] = TagManager.instance!!.getInstructor()
        query["department"] = TagManager.instance!!.getDepartment()
        query["category"] = TagManager.instance!!.getCategory()
        if (TagManager.instance!!.searchEmptyClass) {
            query["time_mask"] = classTimeMask
        }
        searchedQuery = text
        app.restService!!.postSearchQuery(query, object : Callback<List<Lecture>> {
            override fun success(lectures: List<Lecture>?, response: Response) {
                Log.d(TAG, "post search query success!!")
                setSearchedLectures(lectures!!)
                callback.success(lectures, response)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "post search query failed!!")
                callback.failure(error)
            }
        })
    }

    fun addProgressBar() {
//        Refactoring FIXME:
//        searchedLectures.add(null)
    }

    fun removeProgressBar() {
        searchedLectures.removeAt(searchedLectures.size - 1)
    }

    fun loadData(offset: Int, callback: Callback<List<Lecture>>) {
        val query: MutableMap<Any?, Any?> = HashMap<Any?, Any?>()
        query["year"] = PrefManager.instance!!.currentYear
        query["semester"] = PrefManager.instance!!.currentSemester
        query["title"] = searchedQuery
        query["classification"] = TagManager.instance!!.getClassification()
        query["credit"] = TagManager.instance!!.getCredit()
        query["academic_year"] = TagManager.instance!!.getAcademic_year()
        query["instructor"] = TagManager.instance!!.getInstructor()
        query["department"] = TagManager.instance!!.getDepartment()
        query["category"] = TagManager.instance!!.getCategory()
        query["offset"] = offset
        query["limit"] = 20
        if (TagManager.instance!!.searchEmptyClass) {
            query["time_mask"] = classTimeMask
        }
        app.restService!!.postSearchQuery(query, object : Callback<List<Lecture>> {
            override fun success(lectureList: List<Lecture>?, response: Response) {
                Log.d(TAG, "post search query success!!")
                removeProgressBar()
                for (lecture in lectureList!!) {
                    searchedLectures.add(lecture)
                }
                callback.success(lectureList, response)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "post search query failed!!")
                callback.failure(error)
            }
        })
    }

    val classTimeMask: IntArray
        get() {
            val masks = IntArray(7)
            for (lecture in lectures) {
                for (i in 0 until lecture.class_time_mask!!.size()) {
                    val mask = lecture.class_time_mask!![i].asInt
                    masks[i] = masks[i] or mask
                }
            }
            for (i in 0..6) {
                masks[i] = masks[i] xor 0x3FFFFFFF
            }
            return masks
        }

    fun getLectureById(id: String): Lecture? {
        for (lecture in lectures) {
            if (lecture.id == id) return lecture
        }
        return null
    }

    fun clearSearchedLectures() {
        searchedLectures.clear()
        selectedLecture = null
        notifyLecturesChanged()
        notifySearchedLecturesChanged()
    }

    // for color list
    fun fetchColorList(name: String?, callback: Callback<Any>?) {
        app.restService!!.getColorList(name, object : Callback<ColorList> {
            override fun success(colorList: ColorList?, response: Response) {
                Log.d(TAG, "get color list request success")
                setColors(colorList!!.colors!!)
                setColorNames(colorList.names!!)
                val colorListJson = Gson().toJson(this@LectureManager.colorList)
                val colorNameListJson = Gson().toJson(colorNameList)
                PrefManager.instance!!.lectureColors = colorListJson
                PrefManager.instance!!.lectureColorNames = colorNameListJson
                notifyLecturesChanged()
                callback?.success(colorList, response)
            }

            override fun failure(error: RetrofitError) {
                Log.d(TAG, "get color list request failed")
                callback?.failure(error)
            }
        })
    }

    private fun setColors(colors: List<Color>) {
        colorList = colors
    }

    private fun setColorNames(colorNames: List<String>) {
        colorNameList = colorNames
    }

    fun getBgColorByIndex(index: Int): Int {
        return if (colorList!!.size == 0) DEFAULT_BG[index - 1] else colorList!![index - 1].bgColor
    }

    fun getFgColorByIndex(index: Int): Int {
        return if (colorList!!.size == 0) DEFAULT_FG[index - 1] else colorList!![index - 1].fgColor
    }

    fun getColorNameByIndex(index: Int): String {
        return if (colorNameList!!.size == 0) DEFAULT_NAME[index - 1] else colorNameList!![index - 1]
    }

    val defaultBgColor: Int
        get() = DEFAULT_BG[DEFAULT_BG.size - 1]
    val defaultFgColor: Int
        get() = DEFAULT_FG[DEFAULT_FG.size - 1]
    val defaultColorName: String
        get() = DEFAULT_NAME[DEFAULT_NAME.size - 1]

    private fun isEqualLecture(lec1: Lecture, lec2: Lecture): Boolean {
        if (lec1.isCustom) { // custom lecture 면 id 로 비교
            return if (lec1.id == lec2.id) true else false
        }
        return if (lec1.course_number == lec2.course_number && lec1.lecture_number == lec2.lecture_number) true else false
    }

    private fun isDuplicatedClassTime(lec1: Lecture, lec2: Lecture): Boolean {
        for (element1 in lec1.class_time_json!!) {
            val class1 = element1.asJsonObject
            val day1 = class1["day"].asInt
            val start1 = class1["start"].asFloat
            val len1 = class1["len"].asFloat
            val end1 = start1 + len1
            for (element2 in lec2.class_time_json!!) {
                val class2 = element2.asJsonObject
                val day2 = class2["day"].asInt
                val start2 = class2["start"].asFloat
                val len2 = class2["len"].asFloat
                val end2 = start2 + len2
                if (day1 != day2) continue
                if (!(end1 <= start2 || end2 <= start1)) return true
            }
        }
        return false
    }

    private fun notifyLecturesChanged() {
        for (listener in listeners) {
            listener.notifyLecturesChanged()
        }
    }

    private fun notifySearchedLecturesChanged() {
        for (listener in listeners) {
            listener.notifySearchedLecturesChanged()
        }
    }

    companion object {
        private const val TAG = "LECTURE_MANAGER"
        private val DEFAULT_NAME = arrayOf(
                "석류",
                "감귤",
                "들국",
                "완두",
                "비취",
                "지중해",
                "하늘",
                "라벤더",
                "자수정",
                "직접 지정하기"
        )
        private val DEFAULT_FG = intArrayOf(-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0xcccccd)
        private val DEFAULT_BG = intArrayOf(-0x1abba7, -0xa72c3, -0x53ad3, -0x5926d0, -0xd43c9a, -0xe42f37, -0xe26617, -0xb0b73c, -0x50a94d, -0x1f1f20)
        private var singleton: LectureManager? = null
        fun getInstance(app: SNUTTApplication): LectureManager? {
            if (singleton == null) {
                singleton = LectureManager(app)
            }
            return singleton
        }

        @JvmStatic
        val instance: LectureManager?
            get() {
                if (singleton == null) Log.e(TAG, "This method should not be called at this time!!")
                return singleton
            }
    }

    /**
     * LectureManager 싱글톤
     */
    init {
        lectures = ArrayList()
        searchedLectures = ArrayList()
        loadColorData()
    }
}