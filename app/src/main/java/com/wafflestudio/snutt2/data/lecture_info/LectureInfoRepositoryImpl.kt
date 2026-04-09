package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.domainmodel.Building
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.error.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureInfoRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : LectureInfoRepository {

    override suspend fun getSyllabusUrl(
        courseBook: CourseBook,
        lecture: LectureSyllabusInfo,
    ): Result<String> {
        try {
            val url = api._getCoursebooksOfficial(courseBook.year, courseBook.semester, lecture.courseNumber, lecture.lectureNumber).url
            return Result.Success(url)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getReviewInfo(lecture: SyllabusLecture): Result<LectureReviewInfo?> {
        try {
            val dto = api._getLectureReviewSummary(lecture.originalLectureId)
            return Result.Success(LectureReviewInfo(id = dto.id, rating = dto.rating, reviewCount = dto.reviewCount ?: 0))
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getBuildings(lecture: Lecture): Result<List<Building>> {
        val joined = lecture.lectureSessions.map { it.place }.distinct().joinToString(",")
        if (joined.isBlank()) return Result.Success(emptyList())
        try {
            val response = api._getBuildings(joined)
            return Result.Success(response.content.map { it.toDomain() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }
}
