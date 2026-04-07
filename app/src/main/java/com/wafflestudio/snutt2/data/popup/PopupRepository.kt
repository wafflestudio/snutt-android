package com.wafflestudio.snutt2.data.popup

import com.wafflestudio.snutt2.domainmodel.Popup
import com.wafflestudio.snutt2.lib.network.Result
import kotlinx.coroutines.flow.StateFlow

interface PopupRepository {

    val popups: StateFlow<List<Popup>?>

    suspend fun ensurePopupsFetched(): Result<Unit>

    suspend fun closePopupWithHiddenDays(): Result<Unit>

    suspend fun closePopup(): Result<Unit>
}
