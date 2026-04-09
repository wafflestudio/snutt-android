package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.domainmodel.Building
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.data.Result

interface LectureInfoRepository {

    suspend fun getSyllabusUrl(courseBook: CourseBook, lecture: LectureSyllabusInfo): Result<String>

    suspend fun getReviewInfo(lecture: SyllabusLecture): Result<LectureReviewInfo?>

    suspend fun getBuildings(lecture: Lecture): Result<List<Building>>
}
