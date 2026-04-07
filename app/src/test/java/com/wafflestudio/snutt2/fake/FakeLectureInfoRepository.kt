package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.lecture_info.LectureInfoRepository
import com.wafflestudio.snutt2.domainmodel.Building
import com.wafflestudio.snutt2.domainmodel.CourseBook
import com.wafflestudio.snutt2.domainmodel.Lecture
import com.wafflestudio.snutt2.domainmodel.LectureReviewInfo
import com.wafflestudio.snutt2.domainmodel.LectureSyllabusInfo
import com.wafflestudio.snutt2.domainmodel.SyllabusLecture
import com.wafflestudio.snutt2.lib.network.Result

class FakeLectureInfoRepository : LectureInfoRepository {

    // --- 테스트 제어용 필드 ---
    var getSyllabusUrlResult: Result<String> = Result.Success("")
    var getSyllabusUrlCalledWith: Pair<CourseBook, LectureSyllabusInfo>? = null
        private set

    var getBuildingsResult: Result<List<Building>> = Result.Success(emptyList())
    var getBuildingsCalledWith: Lecture? = null
        private set

    // --- 인터페이스 구현 ---
    override suspend fun getSyllabusUrl(courseBook: CourseBook, lecture: LectureSyllabusInfo): Result<String> {
        getSyllabusUrlCalledWith = courseBook to lecture
        return getSyllabusUrlResult
    }

    override suspend fun getBuildings(lecture: Lecture): Result<List<Building>> {
        getBuildingsCalledWith = lecture
        return getBuildingsResult
    }

    // --- 미사용 메서드 ---
    override suspend fun getReviewInfo(lecture: SyllabusLecture): Result<LectureReviewInfo?> = TODO("Not used in this test")
}
