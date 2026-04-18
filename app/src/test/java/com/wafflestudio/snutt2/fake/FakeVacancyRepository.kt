package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.vacancynoti.VacancyRepository
import com.wafflestudio.snutt2.domain.model.Lecture
import com.wafflestudio.snutt2.domain.model.SearchedLecture
import kotlinx.coroutines.flow.MutableStateFlow

class FakeVacancyRepository : VacancyRepository {

    // --- StateFlow ---
    override val firstVacancyVisit = MutableStateFlow(false)
    override val firstVacancyAdd = MutableStateFlow(false)
    override val vacancyLectures = MutableStateFlow<List<SearchedLecture>>(emptyList())

    // --- 테스트 제어용 필드 ---
    var fetchVacancyLecturesResult: Result<Unit> = Result.Success(Unit)

    var removeVacancyLectureResult: Result<Unit> = Result.Success(Unit)
    var removeVacancyLectureCalledWith: MutableList<Lecture> = mutableListOf()
        private set

    var setVacancyVisitedResult: Result<Unit> = Result.Success(Unit)
    var setVacancyVisitedCalled = false
        private set

    // --- 인터페이스 구현 ---
    override suspend fun fetchVacancyLectures(): Result<Unit> = fetchVacancyLecturesResult

    override suspend fun removeVacancyLecture(lecture: Lecture): Result<Unit> {
        removeVacancyLectureCalledWith.add(lecture)
        return removeVacancyLectureResult
    }

    override suspend fun setVacancyVisited(): Result<Unit> {
        setVacancyVisitedCalled = true
        return setVacancyVisitedResult
    }

    var addVacancyLectureResult: Result<Unit> = Result.Success(Unit)
    var addVacancyLectureCalledWith: Lecture? = null
        private set

    override suspend fun addVacancyLecture(lecture: Lecture): Result<Unit> {
        addVacancyLectureCalledWith = lecture
        return addVacancyLectureResult
    }

    // --- 미사용 메서드 ---
    override suspend fun isVacancyRegistered(lecture: Lecture): Result<Boolean> = TODO("Not used in this test")
    override suspend fun isLectureVacancyRegistered(lecture: Lecture): Result<Boolean> = TODO("Not used in this test")
    override suspend fun setVacancyAdded() = TODO("Not used in this test")
}
