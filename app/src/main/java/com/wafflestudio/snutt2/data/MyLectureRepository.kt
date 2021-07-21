package com.wafflestudio.snutt2.data

import com.wafflestudio.snutt2.lib.Optional
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.*
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.toOptional
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyLectureRepository @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage
) {
    private var _currentTable: TableDto
        get() = storage.lastViewedTable.getValue().get()!!
        set(value) = storage.lastViewedTable.setValue(value.toOptional())

    val currentTable: Observable<TableDto> =
        storage.lastViewedTable.asObservable().distinctUntilChanged()
            .filterEmpty()

    fun getCurrentTable(): Optional<TableDto> {
        return storage.lastViewedTable.getValue()
    }


    fun addLecture(lectureId: String): Single<PostCustomLectureResults> {
        return api.postAddLecture(
            _currentTable.id,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _currentTable = it
            }
    }

    fun removeLecture(lectureId: String): Single<DeleteLectureResults> {
        return api.deleteLecture(
            _currentTable.id,
            lectureId
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _currentTable = it
            }
    }

    fun createLecture(lecture: PostCustomLectureParams): Single<PostCustomLectureResults> {
        return api.postCustomLecture(_currentTable.id, lecture)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _currentTable = it
            }
    }

    fun resetLecture(lectureId: String): Single<ResetLectureResults> {
        return api.resetLecture(_currentTable.id, lectureId)
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _currentTable = it
            }
    }

    fun updateLecture(lectureId: String, target: PutLectureParams): Single<PutLectureResults> {
        return api.putLecture(
            _currentTable.id,
            lectureId,
            target
        )
            .subscribeOn(Schedulers.io())
            .doOnSuccess {
                _currentTable = it
            }
    }

    fun getLectureCourseBookUrl(
        courseNumber: String,
        lectureNumber: String,
    ): Single<GetCoursebooksOfficialResults> {
        return api.getCoursebooksOfficial(
            _currentTable.year,
            _currentTable.semester,
            courseNumber,
            lectureNumber
        )
            .subscribeOn(Schedulers.io())
    }
}
