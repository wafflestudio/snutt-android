package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.domain.model.diary.CourseBookDiarySubmissions
import com.wafflestudio.snutt2.domain.model.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.domain.model.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domain.model.diary.DiaryQuestion
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary

// TODO: Diary 관련해서는 data layer 다 한 repository 로 통일하기
interface DiaryRepository {
    suspend fun getDailyClassTypes(): Result<List<DiaryDailyClassType>>

    suspend fun getQuestionnaire(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
    ): Result<DiaryQuestionnaireData>

    suspend fun submitDiary(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit>

    suspend fun getMyDiarySubmissions(): Result<List<CourseBookDiarySubmissions>>

    suspend fun removeDiarySubmission(diary: DiarySummary): Result<Unit>
}

data class DiaryQuestionnaireData(
    val courseTitle: String,
    val questions: List<DiaryQuestion>,
    val nextLectureId: String?,
    val nextLectureTitle: String?,
)
