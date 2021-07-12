package com.wafflestudio.snutt2.manager

import android.util.Log
import com.google.gson.Gson
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.LectureDto
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.lang.RuntimeException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

/**
 * Created by makesource on 2016. 2. 7..
 */
@Singleton
class LectureManager @Inject constructor(
    private val snuttRestApi: SNUTTRestApi,
    private val tagManager: TagManager,
    private val storage: SNUTTStorage
) {
    private var lectures: MutableList<LectureDto> = ArrayList()
    private var selectedLecture: LectureDto? = null
    var currentLecture: LectureDto? = null
    private var searchedQuery: String? = null

    // Search query
    private var searchedLectures: MutableList<LectureDto?> = ArrayList()

    // color list
    var colorList: List<ColorDto> = listOf()
        private set
    var colorNameList: List<String> = listOf()
        private set

//    init {
//        loadColorData()
//    }
//
//    private fun loadColorData() {
//        val type1 = object : TypeToken<List<ColorDto?>?>() {}.type
//        colorList = Gson().fromJson(storage.lectureColors, type1)
//        val type2 = object : TypeToken<List<String?>?>() {}.type
//        colorNameList = Gson().fromJson(storage.lectureColorNames, type2)
//        if (colorList == null) {
//            colorList = ArrayList()
//        }
//        if (colorNameList == null) {
//            colorNameList = ArrayList()
//        }
//    }

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

    fun getLectures(): List<LectureDto> {
        return lectures
    }

    fun getSearchedLectures(): List<LectureDto?> {
        return searchedLectures
    }

    fun setLectures(lecture_list: List<LectureDto>) {
        lectures.clear()
        for (lecture in lecture_list) {
            lectures.add(lecture)
        }
        notifyLecturesChanged()
    }

    fun setSearchedLectures(lecture_list: List<LectureDto>) {
        searchedLectures.clear()
        for (lecture in lecture_list) {
            searchedLectures.add(lecture)
        }
        notifySearchedLecturesChanged()
    }

    fun getSelectedLecture(): LectureDto? {
        return selectedLecture
    }

    fun setSelectedLecture(selectedLecture: LectureDto?) {
        this.selectedLecture = selectedLecture
        notifyLecturesChanged()
    }

    val selectedPosition: Int
        get() = if (selectedLecture == null) -1 else searchedLectures.indexOf(selectedLecture)

    // this is for searched lecture
    fun addLecture(lec: LectureDto): Single<TableDto> {
        val token = storage.accessToken.getValue()
        val id = storage.lastViewedTable.getValue().get()?.id ?: return Single.error(
            RuntimeException("no last viewed table")
        )

        val lectureId = lec.id
        return snuttRestApi.postAddLecture(
            token,
            id,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                storage.lastViewedTable.setValue(it.toOptional())
                setLectures(it.lectureList)
                notifyLecturesChanged()
            }
            .doOnError {
                Log.e(TAG, "post lecture request failed ...")
            }
    }

    // this is for searched lecture
    fun removeLecture(target: LectureDto): Single<TableDto> {
        for (lecture in lectures) {
            if (isEqualLecture(target, lecture)) {
                return removeLecture(lecture.id)
            }
        }
        return Single.error(Throwable("lecture is not exist!!"))
    }

    // this is for custom lecture
    fun createLecture(lecture: PostCustomLectureParams): Single<TableDto> {
        val token = storage.accessToken.getValue()
        val id = storage.lastViewedTable.getValue().get()?.id ?: return Single.error(
            RuntimeException("no last viewed table")
        )

        return snuttRestApi.postCustomLecture(token, id, lecture)
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.setValue(result.toOptional())
                setLectures(result.lectureList)
                notifyLecturesChanged()
            }
    }

    fun removeLecture(lectureId: String): Single<TableDto> {
        val token = storage.accessToken.getValue()
        val id = storage.lastViewedTable.getValue().get()?.id ?: return Single.error(
            RuntimeException("no last viewed table")
        )
        return snuttRestApi.deleteLecture(
            token,
            id,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.setValue(result.toOptional())
                setLectures(result.lectureList)
                currentLecture = null
                notifyLecturesChanged()
            }
    }

    // reset lecture from my lecture list
    // _id 는 유지된다
    fun resetLecture(lectureId: String): Single<TableDto> {
        val token = storage.accessToken.getValue()
        val id = storage.lastViewedTable.getValue().get()?.id ?: return Single.error(
            RuntimeException("no last viewed table")
        )
        return snuttRestApi.resetLecture(
            token,
            id,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.setValue(result.toOptional())
                setLectures(result.lectureList)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
            }
    }

    fun updateLecture(lectureId: String, target: PutLectureParams): Single<TableDto> {
        val token = storage.accessToken.getValue()
        val id = storage.lastViewedTable.getValue().get()?.id ?: return Single.error(
            RuntimeException("no last viewed table")
        )
        return snuttRestApi.putLecture(
            token,
            id,
            lectureId,
            target
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess { result ->
                storage.lastViewedTable.setValue(result.toOptional())
                setLectures(result.lectureList)
                currentLecture = getLectureById(lectureId)
                notifyLecturesChanged()
            }
    }

    fun getCoursebookUrl(
        courseNumber: String,
        lectureNumber: String,
    ): Single<GetCoursebooksOfficialResults> {
        val year = storage.lastViewedTable.getValue().get()?.year ?: return Single.error(
            RuntimeException("no last viewed table")
        )
        val semester = storage.lastViewedTable.getValue().get()?.semester ?: return Single.error(
            RuntimeException("no last viewed table")
        )

        return snuttRestApi.getCoursebooksOfficial(
            year = year,
            semester = semester,
            courseNumber = courseNumber,
            lectureNumber = lectureNumber
        )
            .subscribeOn(Schedulers.io())
    }

    // 내 강의에 이미 들어있는지 -> course_number, lecture_number 비교
    fun alreadyOwned(lec: LectureDto): Boolean {
        for (lecture in lectures) {
            if (isEqualLecture(lecture, lec)) return true
        }
        return false
    }

    fun postSearchQuery(text: String?): Single<List<LectureDto>> {
        searchedQuery = text
        val param = PostSearchQueryParams(
            year = 1, // storage.currentYear.toLong(),
            semester = 1, // storage.currentSemester.toLong(),
            title = text,
            classification = tagManager.getClassification(),
            credit = tagManager.getCredit().map { it.toLong() },
            academic_year = tagManager.getAcademic_year(),
            instructor = tagManager.getInstructor(),
            department = tagManager.getDepartment(),
            category = tagManager.getCategory(),
            time_mask = classTimeMask.toList().map { it.toLong() },
        )
        return snuttRestApi.postSearchQuery(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post search query success!!")
                setSearchedLectures(it)
            }
            .doOnError {
                Log.d(TAG, "post search query failed!!")
            }
    }

//    fun addProgressBar() {
// //        Refactoring FIXME: 더러운 코드
//        searchedLectures.add(null)
//    }

//    fun removeProgressBar() {
//        searchedLectures.removeAt(searchedLectures.size - 1)
//    }

    fun loadData(offset: Int): Single<List<LectureDto>> {
        val param = PostSearchQueryParams(
            year = 1, // storage.currentYear.toLong(),
            semester = 1, // storage.currentSemester.toLong(),
            title = searchedQuery,
            classification = tagManager.getClassification(),
            credit = tagManager.getCredit().map { it.toLong() },
            academic_year = tagManager.getAcademic_year(),
            instructor = tagManager.getInstructor(),
            department = tagManager.getDepartment(),
            category = tagManager.getCategory(),
            time_mask = classTimeMask.toList().map { it.toLong() },
            offset = offset.toLong(),
            limit = 20
        )

        return snuttRestApi.postSearchQuery(param)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "post search query success!!")
                searchedLectures.addAll(it)
            }
            .doOnError {
                Log.d(TAG, "post search query failed!!")
            }
    }

    val classTimeMask: IntArray
        get() {
            val masks = IntArray(7)
            for (lecture in lectures) {
                for (i in lecture.class_time_mask.indices) {
                    val mask = lecture.class_time_mask[i]
                    masks[i] = masks[i] or mask.toInt()
                }
            }
            for (i in 0..6) {
                masks[i] = masks[i] xor 0x3FFFFFFF
            }
            return masks
        }

    fun getLectureById(id: String): LectureDto? {
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
        return snuttRestApi.getColorList(name!!)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                Log.d(TAG, "get color list request success")
                setColors(it.colors)
                setColorNames(it.names)
                val colorListJson = Gson().toJson(this@LectureManager.colorList)
                val colorNameListJson = Gson().toJson(colorNameList)
//                storage.lectureColors = colorListJson
//                storage.lectureColorNames = colorNameListJson
                notifyLecturesChanged()
            }
            .doOnError {
                Log.d(TAG, "get color list request failed")
            }
    }

    private fun setColors(colors: List<ColorDto>) {
        colorList = colors
    }

    private fun setColorNames(colorNames: List<String>) {
        colorNameList = colorNames
    }

    fun getBgColorByIndex(index: Int): Int {
        return if (colorList.size == 0) DEFAULT_BG[index - 1] else colorList[index - 1].bgColor
    }

    fun getFgColorByIndex(index: Int): Int {
        return if (colorList.size == 0) DEFAULT_FG[index - 1] else colorList[index - 1].fgColor
    }

    fun getColorNameByIndex(index: Int): String {
        return if (colorNameList.size == 0) DEFAULT_NAME[index - 1] else colorNameList[index - 1]
    }

    private fun isEqualLecture(lec1: LectureDto, lec2: LectureDto): Boolean {
        if (lec1.isCustom) { // custom lecture 면 id 로 비교
            return if (lec1.id == lec2.id) true else false
        }
        return if (lec1.course_number == lec2.course_number && lec1.lecture_number == lec2.lecture_number) true else false
    }

    private fun isDuplicatedClassTime(lec1: LectureDto, lec2: LectureDto): Boolean {
        for (classTimeDto1 in lec1.class_time_json) {
            val day1 = classTimeDto1.day
            val start1 = classTimeDto1.start
            val len1 = classTimeDto1.len
            val end1 = start1 + len1
            for (classTimeDto2 in lec2.class_time_json) {
                val day2 = classTimeDto2.day
                val start2 = classTimeDto2.start
                val len2 = classTimeDto2.len
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
        private val DEFAULT_FG =
            intArrayOf(-0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0x1, -0xcccccd)
        private val DEFAULT_BG = intArrayOf(
            -0x1abba7,
            -0xa72c3,
            -0x53ad3,
            -0x5926d0,
            -0xd43c9a,
            -0xe42f37,
            -0xe26617,
            -0xb0b73c,
            -0x50a94d,
            -0x1f1f20
        )
    }
}
