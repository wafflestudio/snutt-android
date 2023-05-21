package com.wafflestudio.snutt2.views.logged_in.home.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.lib.network.dto.core.CourseBookDto
import com.wafflestudio.snutt2.lib.network.dto.core.SharedTableDto
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleSharedTableDto
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ShareTableViewModel @Inject constructor(
    private val tableRepository: TableRepository,
) : ViewModel() {

    private val _sharedTableList: MutableStateFlow<List<SimpleSharedTableDto>?> = MutableStateFlow(null)

    val sharedTableList = _sharedTableList
        .map { listOptional ->
            listOptional?.groupBy {
                CourseBookDto(it.semester, it.year)
            }?.entries?.toList()
        }.stateIn(
            viewModelScope, SharingStarted.Lazily, null
        )

    suspend fun fetchSharedTableList() {
        _sharedTableList.emit(tableRepository.fetchSharedTableList())
    }

    suspend fun createSharedTable(tableId: String, title: String) {
        tableRepository.createSharedTable(title, tableId)
    }

    suspend fun getSharedTableById(tableId: String): SharedTableDto {
        return tableRepository.getSharedTableById(tableId)
    }

    suspend fun deleteSharedTable(tableId: String) {
        tableRepository.deleteSharedTable(tableId)
    }

    suspend fun changeSharedTableTitle(tableId: String, newTitle: String) {
        tableRepository.updateSharedTableTitle(tableId, newTitle)
    }

    suspend fun copySharedTable(tableId: String) {
        tableRepository.copySharedTable(tableId)
    }

    suspend fun createShareLink(tableId: String): String {
        return tableRepository.createShareLink(tableId)
    }
}
