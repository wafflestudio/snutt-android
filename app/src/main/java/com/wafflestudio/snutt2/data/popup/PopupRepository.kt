package com.wafflestudio.snutt2.data.popup

import com.wafflestudio.snutt2.lib.network.Result

interface PopupRepository {

    suspend fun fetchAndSetPopup(): Result<Unit>

    suspend fun closePopupWithHiddenDays(): Result<Unit>

    suspend fun closePopup(): Result<Unit>
}
