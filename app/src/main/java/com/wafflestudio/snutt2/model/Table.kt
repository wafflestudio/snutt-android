package com.wafflestudio.snutt2.model

import android.util.Log

/**
 * Created by makesource on 2016. 1. 16..
 */
class Table : Comparable<Table> {
    var id: String? = null
    var year = 0
    var semester = 0
    var title: String? = null
    var lecture_list: List<Lecture>? = null

    constructor(id: String?, year: Int, semester: Int, title: String?, lecture_list: List<Lecture>?) {
        this.id = id
        this.year = year
        this.semester = semester
        this.title = title
        this.lecture_list = lecture_list
    }

    constructor(id: String?, title: String?) {
        this.id = id
        year = 0
        semester = 0
        this.title = title
        lecture_list = null
    }

    constructor() {}

    val fullSemester: String
        get() {
            val yearString: String
            val semesterString: String
            yearString = year.toString()
            when (semester) {
                1 -> semesterString = "1"
                2 -> semesterString = "S"
                3 -> semesterString = "2"
                4 -> semesterString = "W"
                else -> {
                    semesterString = ""
                    Log.e(TAG, "semester is out of range!!")
                }
            }
            return "$yearString-$semesterString"
        }

    /**
     * Compares this object to the specified object to determine their relative
     * order.
     *
     * @param another the object to compare to this instance.
     * @return a negative integer if this instance is less than `another`;
     * a positive integer if this instance is greater than
     * `another`; 0 if this instance has the same order as
     * `another`.
     * @throws ClassCastException if `another` cannot be converted into something
     * comparable to `this` instance.
     */
    override fun compareTo(another: Table): Int {
        if (year > another.year) return -1
        if (year < another.year) return 1
        if (year == another.year) {
            if (semester > another.semester) return -1
            if (semester < another.semester) return 1
            if (semester == another.semester) {
                // update time 기준으로 비교!
                return 0
            }
        }
        return 0
    }

    companion object {
        private const val TAG = "MODEL_TABLE"
    }
}