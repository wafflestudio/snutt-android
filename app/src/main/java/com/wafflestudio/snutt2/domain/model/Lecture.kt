package com.wafflestudio.snutt2.domain.model

sealed class Lecture {
    abstract val id: String
    abstract val courseTitle: String
    abstract val lectureSessions: List<LectureSession>
    abstract val instructor: String
    abstract val credit: Long
    abstract val remark: String
}

sealed class LocalLecture(
    override val color: LectureColor,
) : Lecture(),
    LectureUIInfo

data class SearchedLecture(
    override val id: String,
    override val courseTitle: String,
    override val lectureSessions: List<LectureSession>,
    override val instructor: String,
    override val credit: Long,
    override val remark: String,

    override val classification: String,
    override val department: String,
    override val academicYear: String,
    override val courseNumber: String,
    override val lectureNumber: String,
    override val category: String,
    override val categoryPre2025: String,
    override val quota: Long,
    override val freshmanQuota: Long,

    override val registrationCount: Long,
    override val wasFull: Boolean,

    // FIXME: 사실 reviewInfo 는 SyllabusLecture 도 가진다.
    // 하지만 SearchedLecture 는 태생부터 reviewInfo 정보를 전부 가지는 반면,
    // SyllabusLecture 는 처음에는 reviewInfo.id 값만 있고 rating 과 reviewCount 는 별도 API 로 불러와야 한다. (originalLectureId 사용)
    val reviewInfo: LectureReviewInfo,
) : Lecture(),
    LectureSyllabusInfo,
    LectureVacancyInfo

data class SyllabusLecture(
    override val id: String,
    override val courseTitle: String,
    override val lectureSessions: List<LectureSession>,
    override val instructor: String,
    override val credit: Long,
    override val remark: String,

    override val color: LectureColor,

    override val classification: String,
    override val department: String,
    override val academicYear: String,
    override val courseNumber: String,
    override val lectureNumber: String,
    override val category: String,
    override val categoryPre2025: String, // TODO: Nullable?
    override val quota: Long,
    override val freshmanQuota: Long, // TODO: Nullable?

    val originalLectureId: String,
) : LocalLecture(color),
    LectureSyllabusInfo

data class CustomLecture(
    override val id: String,
    override val courseTitle: String,
    override val lectureSessions: List<LectureSession>,
    override val instructor: String,
    override val credit: Long,
    override val remark: String,

    override val color: LectureColor,
) : LocalLecture(color) {
    companion object {
        val Empty = CustomLecture(
            id = "",
            courseTitle = "",
            lectureSessions = emptyList(),
            instructor = "",
            credit = 0L,
            remark = "",
            color = LectureColor.BuiltIn(0),
        )
    }
}

interface LectureSyllabusInfo {
    val classification: String
    val department: String
    val academicYear: String
    val courseNumber: String
    val lectureNumber: String
    val category: String
    val categoryPre2025: String
    val quota: Long
    val freshmanQuota: Long
}

interface LectureVacancyInfo {
    val registrationCount: Long
    val wasFull: Boolean
}

interface LectureUIInfo {
    val color: LectureColor
}
