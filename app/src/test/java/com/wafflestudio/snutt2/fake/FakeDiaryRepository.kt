package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.lecturediary.DiaryQuestionnaireData
import com.wafflestudio.snutt2.data.lecturediary.DiaryRepository
import com.wafflestudio.snutt2.domain.model.CourseBook
import com.wafflestudio.snutt2.domain.model.diary.CourseBookDiarySubmissions
import com.wafflestudio.snutt2.domain.model.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.domain.model.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.domain.model.diary.DiaryTargetLecture
import kotlinx.coroutines.CompletableDeferred

class FakeDiaryRepository : DiaryRepository {

    // region 테스트 제어용 필드

    var getDailyClassTypesResult: Result<List<DiaryDailyClassType>> = Result.Success(emptyList())

    var getQuestionnaireResult: Result<DiaryQuestionnaireData> =
        Result.Success(DiaryQuestionnaireData(courseTitle = "", questions = emptyList(), nextLectureId = null, nextLectureTitle = null))

    var submitDiaryResult: Result<Unit> = Result.Success(Unit)

    var submitDiaryGate: CompletableDeferred<Unit>? = null

    var submitDiaryCallCount = 0
        private set

    // endregion

    override suspend fun getDailyClassTypes(): Result<List<DiaryDailyClassType>> = getDailyClassTypesResult

    override suspend fun getQuestionnaire(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
    ): Result<DiaryQuestionnaireData> = getQuestionnaireResult

    override suspend fun submitDiary(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit> {
        submitDiaryCallCount++
        submitDiaryGate?.await()
        return submitDiaryResult
    }

    override suspend fun getMyDiarySubmissions(): Result<List<CourseBookDiarySubmissions>> = TODO("Not used in this test")

    override suspend fun removeDiarySubmission(diary: DiarySummary): Result<Unit> = TODO("Not used in this test")

    override suspend fun getDiaryTargetLecture(courseBook: CourseBook): Result<DiaryTargetLecture> = TODO("Not used in this test")
}
