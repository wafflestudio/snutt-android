package com.wafflestudio.snutt2.data.popup

import com.wafflestudio.snutt2.domain.model.Popup
import com.wafflestudio.snutt2.data.Result
import kotlinx.coroutines.flow.StateFlow

interface PopupRepository {

    val popups: StateFlow<List<Popup>?>

    suspend fun ensurePopupsFetched(): Result<Unit>

    suspend fun closePopupWithHiddenDays(): Result<Unit>

    suspend fun closePopup(): Result<Unit>
}
