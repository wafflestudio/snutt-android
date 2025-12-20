package com.wafflestudio.snutt2.data.lecture_diary

import com.wafflestudio.snutt2.domainmodel.diary.DiaryDailyClassType
import com.wafflestudio.snutt2.domainmodel.diary.DiaryAnsweredQuestion
import com.wafflestudio.snutt2.lib.network.Result
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.DiaryQuestionnaireRequestDto
import com.wafflestudio.snutt2.lib.network.dto.DiarySubmissionRequestDto
import com.wafflestudio.snutt2.lib.network.dto.core.DiaryQuestionAnswerDto
import com.wafflestudio.snutt2.lib.network.dto.core.toDomainModel
import com.wafflestudio.snutt2.lib.network.toDomainError
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiaryRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : DiaryRepository {
    override suspend fun getDailyClassTypes(): Result<List<DiaryDailyClassType>> {
        return try {
            val result = api._getDailyClassTypes()
            Result.Success(result.map { it.toDomainModel() })
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun getQuestionnaire(
        lectureId: String,
        dailyClassTypes: List<String>,
    ): Result<DiaryQuestionnaireData> {
        return try {
            val result = api._getQuestionnaireFromActivities(
                DiaryQuestionnaireRequestDto(
                    lectureId = lectureId,
                    dailyClassTypes = dailyClassTypes,
                ),
            )
            Result.Success(
                DiaryQuestionnaireData(
                    lectureTitle = result.lectureTitle,
                    questions = result.questions.map { it.toDomainModel() },
                    nextLectureId = result.nextLectureId,
                    nextLectureTitle = result.nextLectureTitle,
                ),
            )
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun submitDiary(
        lectureId: String,
        dailyClassTypes: List<String>,
        questionAnswers: List<DiaryAnsweredQuestion>,
        comment: String,
    ): Result<Unit> {
        return try {
            api._submitDiary(
                DiarySubmissionRequestDto(
                    lectureId = lectureId,
                    dailyClassTypes = dailyClassTypes,
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

    override suspend fun getMyDiarySubmissions(): Result<List<com.wafflestudio.snutt2.domainmodel.diary.CourseBookDiarySubmissions>> {
        return try {
            val result = api._getMyDiarySubmissions()
            Result.Success(
                result
                    .map { it.toDomainModel() }
                    .filter { it.submissions.isNotEmpty() },
            )
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeDiarySubmission(id: String): Result<Unit> {
        return try {
            api._removeDiarySubmission(id)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }
}
