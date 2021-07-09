package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val storage: SNUTTStorage
) : ViewModel() {

    val currentTimetable: Observable<TableDto>
        get() = storage.lastViewedTable
            .asObservable()
            .filterEmpty()

    val trimParam: Observable<TableTrimParam>
        get() = storage.tableTrimParam
            .asObservable()
}
