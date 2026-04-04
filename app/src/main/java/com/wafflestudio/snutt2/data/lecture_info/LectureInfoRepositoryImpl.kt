package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.domainmodel.Building
import com.wafflestudio.snutt2.domainmodel.Campus
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.GeoCoordinate
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto
import com.wafflestudio.snutt2.lib.network.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LectureInfoRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : LectureInfoRepository {

    override suspend fun getSyllabusUrl(
        courseBook: CourseBook,
        courseNumber: String,
        lectureNumber: String,
    ): Result<String> {
        try {
            val url = api._getCoursebooksOfficial(courseBook.year, courseBook.semester, courseNumber, lectureNumber).url
            return Result.Success(url)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?> {
        try {
            val dto = api._getLectureReviewSummary(lectureId)
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
            return Result.Success(response.content.map { it.toDomainModel() })
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    private fun LectureBuildingDto.toDomainModel(): Building = Building(
        campus = when (campus) {
            com.wafflestudio.snutt2.model.Campus.GWANAK -> Campus.GWANAK
            com.wafflestudio.snutt2.model.Campus.YEONGEON -> Campus.YEONGEON
            com.wafflestudio.snutt2.model.Campus.PYEONGCHANG -> Campus.PYEONGCHANG
        },
        buildingNumber = buildingNumber,
        buildingNameKor = buildingNameKor ?: "",
        buildingNameEng = buildingNameEng ?: "",
        coordinate = GeoCoordinate(
            latitude = locationInDMS.latitude,
            longitude = locationInDMS.longitude,
        ),
    )
}
