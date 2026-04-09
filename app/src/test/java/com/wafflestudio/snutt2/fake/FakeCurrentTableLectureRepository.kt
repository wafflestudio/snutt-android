package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.current_table_lecture.CurrentTableLectureRepository
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.data.Result

class FakeCurrentTableLectureRepository : CurrentTableLectureRepository {

    // --- 테스트 제어용 필드 ---
    var addLectureResult: Result<Unit> = Result.Success(Unit)
    var addLectureCalledWith: Pair<SearchedLecture, Boolean>? = null
        private set

    var removeLectureSearchedResult: Result<Unit> = Result.Success(Unit)
    var removeLectureSearchedCalledWith: SearchedLecture? = null
        private set

    // --- 인터페이스 구현 ---
    override suspend fun addLecture(lecture: SearchedLecture, isForced: Boolean): Result<Unit> {
        addLectureCalledWith = lecture to isForced
        return addLectureResult
    }

    override suspend fun removeLecture(lecture: SearchedLecture): Result<Unit> {
        removeLectureSearchedCalledWith = lecture
        return removeLectureSearchedResult
    }

    // --- 미사용 메서드 ---
    override suspend fun removeLecture(lecture: LocalLecture): Result<Unit> = TODO("Not used in this test")
    override suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit> = TODO("Not used in this test")
    override suspend fun resetLecture(lecture: LocalLecture): Result<LocalLecture> = TODO("Not used in this test")
    override suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean): Result<Unit> = TODO("Not used in this test")
}
