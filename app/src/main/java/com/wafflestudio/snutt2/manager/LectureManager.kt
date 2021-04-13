package com.wafflestudio.snutt2.manager

import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.wafflestudio.snutt2.SNUTTApplication
import com.wafflestudio.snutt2.model.Color
import com.wafflestudio.snutt2.model.Lecture
import com.wafflestudio.snutt2.model.Table
import com.wafflestudio.snutt2.network.dto.GetColorListResults
import com.wafflestudio.snutt2.network.dto.GetCoursebooksOfficialResults
import com.wafflestudio.snutt2.network.dto.PostSearchQueryParams
import com.wafflestudio.snutt2.network.dto.core.TempUtil
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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

    // //////
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
    // Refactoring FIXME: 대충 봄
    fun addLecture(lec: Lecture): Single<Table> {
        if (alreadyOwned(lec)) {
            Log.w(TAG, "lecture is duplicated!! ")
            return Single.error(Throwable("lecture is duplicated!!"))
        }
        if (alreadyExistClassTime(lec)) {
            Log.d(TAG, "lecture is duplicated!! ")
            Toast.makeText(app, "시간표의 시간과 겹칩니다", Toast.LENGTH_SHORT).show()
            return Single.error(Throwable("lecture is duplicated!!"))
        }
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        val lectureId = lec.id
        return app.restService!!.postAddLecture(
            token!!,
            id!!,
            lectureId!!)
            .subscribeOn(Schedulers.io())
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess {
                Log.d(TAG, "post lecture request success!!")
                PrefManager.instance!!.updateNewTable(it)
                setLectures(it.lecture_list!!)
                notifyLecturesChanged()
            }
            .doOnError {
                Log.e(TAG, "post lecture request failed ...")
            }
    }

    // this is for searched lecture
    fun removeLecture(target: Lecture): Single<Table> {
        for (lecture in lectures) {
            if (isEqualLecture(target, lecture)) {
                return removeLecture(lecture.id)
            }
        }
        return Single.error(Throwable("lecture is not exist!!"))
    }

    // this is for custom lecture
    fun createLecture(lecture: Lecture?): Single<Table> {
        Log.d(TAG, "create lecture method called!!")
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        return app.restService!!.postCustomLecture(
            token!!,
            id!!,
            TempUtil.toDto(lecture!!)
        )
            .subscribeOn(Schedulers.io())
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess { result ->
                val table = result
                PrefManager.instance!!.updateNewTable(result!!)
                setLectures(table.lecture_list!!)
                notifyLecturesChanged()
            }
    }

    fun removeLecture(lectureId: String?): Single<Table> {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        return app.restService!!.deleteLecture(
            token!!,
            id!!,
            lectureId!!
        )
            .map { TempUtil.toLegacyModel(it) }
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                Log.d(TAG, "remove lecture request success!!")
                PrefManager.instance!!.updateNewTable(result!!)
                setLectures(result.lecture_list!!)
                currentLecture = null
                notifyLecturesChanged()
            }
            .doOnError {
                Log.e(TAG, "remove lecture request failed ...")
            }
    }

    // reset lecture from my lecture list
    // _id 는 유지된다
    fun resetLecture(lectureId: String): Single<Table> {
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        return app.restService!!.resetLecture(
            token!!,
            id!!,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess { result ->
                Log.d(TAG, "reset lecture request success!!")
                PrefManager.instance!!.updateNewTable(result)
                setLectures(result.lecture_list!!)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
            }
            .doOnError {
                Log.e(TAG, "reset lecture request failed ...")
            }
    }

    fun updateLecture(lectureId: String, target: Lecture?): Single<Table> {
        Log.d(TAG, "update lecture method called!!")
        val token = PrefManager.instance!!.prefKeyXAccessToken
        val id = PrefManager.instance!!.lastViewTableId
        return app.restService!!.putLecture(
            token!!,
            id!!,
            lectureId,
            TempUtil.toDto(target!!))
            .subscribeOn(Schedulers.io())
            .map { TempUtil.toLegacyModel(it) }
            .doOnSuccess {
                PrefManager.instance!!.updateNewTable(it!!)
                setLectures(it.lecture_list!!)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
            }
            .doOnError {
                Log.e(TAG, "put lecture request failed..")
            }
    }

    fun getCoursebookUrl(
        courseNumber: String,
        lectureNumber: String,
    ): Single<GetCoursebooksOfficialResults> {
        // Refactor FIXME: currentYear, semester Int to Long
        val year = PrefManager.instance!!.currentYear.toLong()
        val semester = PrefManager.instance!!.currentSemester.toLong()
        return app.restService!!.getCoursebooksOfficial(
            year = year,
            semester = semester,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get coursebook official request success!")
            }
            .doOnError {
                Log.e(TAG, "get coursebook official request failed..")
            }
    }

    // 내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    fun alreadyOwned(lec: Lecture): Boolean {
        for (lecture in lectures) {
            if (isEqualLecture(lecture, lec)) return true
        }
        return false
    }

    // 이미 내 강의에 존재하는 시간인지
    fun alreadyExistClassTime(lec: Lecture): Boolean {
        for (lecture in lectures) {
            if (isDuplicatedClassTime(lecture, lec)) return true
        }
        return false
    }

    // 주어진 요일, 시각을 포함하고 있는지
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

    fun postSearchQuery(text: String?): Single<List<Lecture>> {
        searchedQuery = text
        val param = PostSearchQueryParams(
            year = PrefManager.instance!!.currentYear.toLong(),
            semester = PrefManager.instance!!.currentSemester.toLong(),
            title = text,
            classification = TagManager.instance!!.getClassification(),
            credit = TagManager.instance!!.getCredit().map { it.toLong() },
            academic_year = TagManager.instance!!.getAcademic_year(),
            instructor = TagManager.instance!!.getInstructor(),
            department = TagManager.instance!!.getDepartment(),
            category = TagManager.instance!!.getCategory(),
            time_mask = classTimeMask.toList().map { it.toLong() },
        )
        return app.restService!!.postSearchQuery(param)
            .subscribeOn(Schedulers.io())
            .map { it.map { TempUtil.toLegacyModel(it) } }
            .doOnSuccess {
                Log.d(TAG, "post search query success!!")
                setSearchedLectures(it)
            }
            .doOnError {
                Log.d(TAG, "post search query failed!!")

            }
    }

    fun addProgressBar() {
//        Refactoring FIXME:
//        searchedLectures.add(null)
    }

    fun removeProgressBar() {
        searchedLectures.removeAt(searchedLectures.size - 1)
    }

    fun loadData(offset: Int): Single<List<Lecture>> {
        val param = PostSearchQueryParams(
            year = PrefManager.instance!!.currentYear.toLong(),
            semester = PrefManager.instance!!.currentSemester.toLong(),
            title = searchedQuery,
            classification = TagManager.instance!!.getClassification(),
            credit = TagManager.instance!!.getCredit().map { it.toLong() },
            academic_year = TagManager.instance!!.getAcademic_year(),
            instructor = TagManager.instance!!.getInstructor(),
            department = TagManager.instance!!.getDepartment(),
            category = TagManager.instance!!.getCategory(),
            time_mask = classTimeMask.toList().map { it.toLong() },
            offset = offset.toLong(),
            limit = 20
        )

        return app.restService!!.postSearchQuery(param)
            .map { it.map { TempUtil.toLegacyModel(it) } }
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post search query success!!")
                removeProgressBar()
                for (lecture in it) {
                    searchedLectures.add(lecture)
                }
            }
            .doOnError {
                Log.d(TAG, "post search query failed!!")
            }
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
    fun fetchColorList(name: String?): Single<GetColorListResults> {
        return app.restService!!.getColorList(name!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get color list request success")
                setColors(it.colors.map { TempUtil.toLegacyModel(it) })
                setColorNames(it.names)
                val colorListJson = Gson().toJson(this@LectureManager.colorList)
                val colorNameListJson = Gson().toJson(colorNameList)
                PrefManager.instance!!.lectureColors = colorListJson
                PrefManager.instance!!.lectureColorNames = colorNameListJson
                notifyLecturesChanged()
            }
            .doOnError {
                Log.d(TAG, "get color list request failed")
            }
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
