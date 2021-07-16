package com.wafflestudio.snutt2.views.logged_in.home.timetable

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.MyLectureRepository
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.data.TableRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val tableRepository: TableRepository,
    private val myLectureRepository: MyLectureRepository,
    private val storage: SNUTTStorage,
    private val apiOnError: ApiOnError
) : ViewModel() {

    val currentTimetable: Observable<TableDto>
        get() = myLectureRepository.currentTable

    val trimParam: Observable<TableTrimParam>
        get() = storage.tableTrimParam.asObservable()

    fun fetchLastViewedTable() {
        storage.lastViewedTable.getValue().let { table ->
            if (table.isEmpty()) {
                tableRepository.getDefaultTable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeBy(onError = apiOnError)
            }
        }
    }
}
