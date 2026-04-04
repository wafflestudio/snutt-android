package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.domainmodel.Building
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.lib.network.Result

interface LectureInfoRepository {

    suspend fun getSyllabusUrl(courseBook: CourseBook, courseNumber: String, lectureNumber: String): Result<String>

    suspend fun getReviewInfo(lectureId: String): Result<LectureReviewInfo?>

    suspend fun getBuildings(lecture: Lecture): Result<List<Building>>
}
