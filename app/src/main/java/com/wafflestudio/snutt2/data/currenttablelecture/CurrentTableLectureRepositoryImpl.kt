package com.wafflestudio.snutt2.data.currenttablelecture

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.mapper.toLectureDto
import com.wafflestudio.snutt2.data.mapper.toLocalLecture
import com.wafflestudio.snutt2.domain.Unknown
import com.wafflestudio.snutt2.domain.model.CustomLecture
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.LocalLecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.LectureDto
import com.wafflestudio.snutt2.network.dto.PostCustomLectureParams
import com.wafflestudio.snutt2.network.dto.PostLectureParams
import com.wafflestudio.snutt2.network.error.toDomainError
import com.wafflestudio.snutt2.storage.SNUTTStorage
import com.wafflestudio.snutt2.storage.toOptional
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentTableLectureRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
) : CurrentTableLectureRepository {

    override suspend fun addLecture(lecture: SearchedLecture, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val response = api._postAddLecture(prevTable.id, lecture.id, PostLectureParams(isForced))
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeLecture(lecture: LocalLecture): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val response = api._deleteLecture(prevTable.id, lecture.id)
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun removeLecture(lecture: SearchedLecture): Result<Unit> {
        val table = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        val lectureId = table.lectureList
            .find { it.lecture_id == lecture.id }
            ?.id ?: return Result.Success(Unit)

        return try {
            val response = api._deleteLecture(table.id, lectureId)
            storage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateLecture(lecture: Lecture, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Success(Unit)
        try {
            val params = lecture.toLectureDto().toParams()
            params.isForced = isForced
            val response = api._putLecture(prevTable.id, lecture.id, params)
            storage.lastViewedTable.update(response.toOptional())
            return Result.Success(Unit)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun resetLecture(lecture: LocalLecture): Result<LocalLecture> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Fail(Unknown("", ""))
        try {
            val response = api._resetLecture(prevTable.id, lecture.id)
            storage.lastViewedTable.update(response.toOptional())
            val resetLecture = response.lectureList.find { it.id == lecture.id }!!.toLocalLecture()
            return Result.Success(resetLecture)
        } catch (e: Exception) {
            return Result.Fail(e.toDomainError())
        }
    }

    override suspend fun createCustomLecture(lecture: CustomLecture, isForced: Boolean): Result<Unit> {
        val prevTable = storage.lastViewedTable.get().value
            ?: return Result.Fail(Unknown("", ""))
        return try {
            val params = lecture.toLectureDto().toParams().also { it.isForced = isForced }
            val response = api._postCustomLecture(prevTable.id, params)
            storage.lastViewedTable.update(response.toOptional())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    private fun LectureDto.toParams() = PostCustomLectureParams(
        id = id,
        course_title = course_title,
        instructor = instructor,
        colorIndex = colorIndex,
        color = color,
        department = department,
        academic_year = academic_year,
        credit = credit,
        classification = classification,
        category = category,
        categoryPre2025 = categoryPre2025,
        remark = remark,
        class_time_json = class_time_json,
    )
}
