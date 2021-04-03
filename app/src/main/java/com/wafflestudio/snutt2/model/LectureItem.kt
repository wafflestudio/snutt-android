package com.wafflestudio.snutt2.model

/**
 * Created by makesource on 2016. 9. 4..
 */
class LectureItem {
    enum class Type(val value: Int) {
        Title(0), Instructor(1), Color(2), Department(3), AcademicYear(4), Credit(5), Classification(6), Category(7), CourseNumber(8), LectureNumber(9), Remark(10), ClassTime(11), Syllabus(12), RemoveLecture(13), AddClassTime(14), ResetLecture(15), ShortHeader(16), LongHeader(17), ClassTimeHeader(18), Margin(19);

    }

    enum class ViewType(val value: Int) {
        ItemShortHeader(0), ItemLongHeader(1), ItemTitle(2), ItemButton(3), ItemColor(4), ItemClass(5), ItemRemark(6), ItemClassTimeHeader(7), ItemMargin(8);

    }

    var title1: String? = null
    var value1: String? = null
    var title2: String? = null
    var value2: String? = null
    var colorIndex = 0
    private var color: Color? = null
    var classTime: ClassTime? = null
    var type: Type
    var isEditable = false

    constructor(title1: String?, value1: String?, title2: String?, value2: String?, type: Type) {
        this.title1 = title1
        this.value1 = value1
        this.title2 = title2
        this.value2 = value2
        this.type = type
        isEditable = false
    }

    constructor(title1: String?, value1: String?, type: Type) {
        this.title1 = title1
        this.value1 = value1
        this.type = type
        isEditable = false
    }

    constructor(title1: String?, index: Int, color: Color?, type: Type) {
        this.title1 = title1
        colorIndex = index
        this.color = color
        this.type = type
        isEditable = false
    }

    constructor(classTime: ClassTime?, type: Type, editable: Boolean) {
        this.classTime = classTime
        this.type = type
        isEditable = editable
    }

    constructor(classTime: ClassTime?, type: Type) {
        this.classTime = classTime
        this.type = type
        isEditable = false
    }

    constructor(type: Type, editable: Boolean) {
        this.type = type
        isEditable = editable
    }

    constructor(type: Type) {
        this.type = type
    }

    val viewType: ViewType
        get() = when (type) {
            Type.ShortHeader -> ViewType.ItemShortHeader
            Type.LongHeader -> ViewType.ItemLongHeader
            Type.ClassTimeHeader -> ViewType.ItemClassTimeHeader
            Type.Margin -> ViewType.ItemMargin
            Type.Title, Type.Instructor, Type.Department, Type.AcademicYear, Type.Credit, Type.Classification, Type.Category, Type.CourseNumber, Type.LectureNumber -> ViewType.ItemTitle
            Type.Color -> ViewType.ItemColor
            Type.ClassTime -> ViewType.ItemClass
            Type.Remark -> ViewType.ItemRemark
            else -> ViewType.ItemButton
        }

    fun getColor(): Color? {
        return color
    }

    fun setColor(color: Color?) {
        colorIndex = 0
        this.color = color
    }
}