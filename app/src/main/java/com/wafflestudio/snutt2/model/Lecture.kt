package com.wafflestudio.snutt2.model

import com.google.common.base.Strings
import com.google.gson.JsonArray
import com.wafflestudio.snutt2.SNUTTUtils
import java.text.DecimalFormat

/**
 * Created by makesource on 2016. 1. 16..
 */
class Lecture {
    var id: String? = null
    var classification: String? = null
    var department: String? = null
    var academic_year: String? = null
    var course_number: String? = null
    var lecture_number: String? = null
    var course_title: String? = null
    var credit = 0
    var class_time: String? = null // lecture 검색시 띄어주는 class time
    var class_time_mask: JsonArray? = null
    var class_time_json: JsonArray? = null
    var location: String? = null
    var instructor: String? = null
    var quota = 0
    var enrollment = 0
    var remark: String? = null
    var category: String? = null
    var colorIndex = 0 // 색상
    private var color: Color

    constructor() {
        color = Color()
    }

    constructor(lec: Lecture) {
        id = lec.id
        classification = lec.classification
        department = lec.department
        academic_year = lec.academic_year
        course_number = lec.course_number
        lecture_number = lec.lecture_number
        course_title = lec.course_title
        credit = lec.credit
        class_time = lec.class_time
        class_time_mask = lec.class_time_mask
        class_time_json = lec.class_time_json
        location = lec.location
        instructor = lec.instructor
        quota = lec.quota
        enrollment = lec.enrollment
        remark = lec.remark
        this.category = lec.category
        colorIndex = 0
        color = Color()
    }

    val isCustom: Boolean
        get() = if (Strings.isNullOrEmpty(course_number) &&
            Strings.isNullOrEmpty(lecture_number)
        ) true else false

    fun setColor(color: Color) {
        colorIndex = 0
        this.color = color
    }

    fun getColor(): Color {
        return color
    }

    var bgColor: Int
        get() = color.bgColor
        set(bgColor) {
            color.bgColor = bgColor
        }
    var fgColor: Int
        get() = color.fgColor
        set(fgColor) {
            color.fgColor = fgColor
        }

    // 간소화된 강의 시간
    val simplifiedClassTime: String?
        get() {
            var text = ""
            for (i in 0 until class_time_json!!.size()) {
                val class1 = class_time_json!![i].asJsonObject
                val day = class1["day"].asInt
                val start = class1["start"].asFloat
                val len = class1["len"].asFloat
                val place = class1["place"].asString
                text += SNUTTUtils.numberToWday(day) + decimalFormat.format(start.toDouble())
                if (i != class_time_json!!.size() - 1) text += "/"
            }
            if (Strings.isNullOrEmpty(text)) text = "(없음)"
            return text
        }
    val simplifiedLocation: String?
        get() {
            var text: String? = ""
            for (i in 0 until class_time_json!!.size()) {
                val class1 = class_time_json!![i].asJsonObject
                val day = class1["day"].asInt
                val start = class1["start"].asFloat
                val len = class1["len"].asFloat
                val place = class1["place"].asString
                text += place
                if (i != class_time_json!!.size() - 1) text += "/"
            }
            if (Strings.isNullOrEmpty(text)) text = "(없음)"
            return text
        }

    companion object {
        /*
     * 주의 !
     * 검색시 날아오는 lecture id 와
     * 내 시간표에 추가된 lecture id 는 서로 다른 값
     */
        private val decimalFormat = DecimalFormat()
    }
}
