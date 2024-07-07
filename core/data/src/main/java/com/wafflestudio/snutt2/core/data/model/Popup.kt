package com.wafflestudio.snutt2.core.data.model

import com.wafflestudio.snutt2.core.model.data.Popup
import com.wafflestudio.snutt2.core.network.model.GetPopupResults

fun GetPopupResults.Popup.toExternalModel() = Popup(
    key = this.key,
    imageUri = this.uri,
    popupHideDays = this.popupHideDays,
)