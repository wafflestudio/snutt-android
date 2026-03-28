package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.dto.core.LectureBuildingDto

interface LectureInfoRepository {

    suspend fun getSyllabusUrl(courseBook: CourseBook, courseNumber: String, lectureNumber: String): Result<String>

    suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?>

    suspend fun getBuildings(places: List<String>): Result<List<LectureBuildingDto>>
}
