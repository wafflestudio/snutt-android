package com.wafflestudio.snutt2.data.lecture_info

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.Building
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LectureReviewInfo
import com.wafflestudio.snutt2.domain.model.LectureSyllabusInfo
import com.wafflestudio.snutt2.domain.model.SyllabusLecture

interface LectureInfoRepository {

    suspend fun getSyllabusUrl(courseBook: CourseBook, lecture: LectureSyllabusInfo): Result<String>

    suspend fun getReviewInfo(lecture: SyllabusLecture): Result<LectureReviewInfo?>

    suspend fun getBuildings(lecture: Lecture): Result<List<Building>>
}
