package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domain.model.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.domain.model.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domain.model.diary.DiarySummary
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.DiaryQuestionnaireRequestDto
import com.wafflestudio.snutt2.network.dto.DiarySubmissionRequestDto
import com.wafflestudio.snutt2.network.dto.DiaryQuestionAnswerDto
import com.wafflestudio.snutt2.data.mapper.toDomain
import com.wafflestudio.snutt2.network.error.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : DiaryRepository {
    override suspend fun getDailyClassTypes(): Result<List<DiaryDailyClassType>> {
        return try {
            val result = api._getDailyClassTypes()
            Result.Success(result.map { it.toDomain() })
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getQuestionnaire(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
    ): Result<DiaryQuestionnaireData> {
        return try {
            val result = api._getQuestionnaireFromActivities(
                DiaryQuestionnaireRequestDto(
                    lectureId = lectureId,
                    dailyClassTypes = dailyClassTypes.map { it.name },
                ),
            )
            Result.Success(
                DiaryQuestionnaireData(
                    courseTitle = result.courseTitle,
                    questions = result.questions.map { it.toDomain() },
                    nextLectureId = result.nextLecture?.lectureId,
                    nextLectureTitle = result.nextLecture?.courseTitle,
                ),
            )
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun submitDiary(
        lectureId: String,
        dailyClassTypes: List<DiaryDailyClassType>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit> {
        return try {
            api._submitDiary(
                DiarySubmissionRequestDto(
                    lectureId = lectureId,
                    dailyClassTypes = dailyClassTypes.map { it.name },
                    questionAnswers = questionAnswers.map {
                        DiaryQuestionAnswerDto(
                            questionId = it.questionId,
                            answerIndex = it.answerIndex,
                        )
                    },
                    comment = comment,
                ),
            )
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getMyDiarySubmissions(): Result<List<com.wafflestudio.snutt2.domain.model.diary.CourseBookDiarySubmissions>> {
        return try {
            val result = api._getMyDiarySubmissions()
            Result.Success(
                result
                    .map { it.toDomain() }
                    .filter { it.submissions.isNotEmpty() },
            )
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeDiarySubmission(diary: DiarySummary): Result<Unit> {
        return try {
            api._removeDiarySubmission(diary.id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }
}
