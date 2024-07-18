package com.wafflestudio.snutt2.views.logged_in.home.popups

import com.wafflestudio.snutt2.core.network.model.GetPopupResults

class PopupState {
    var popup: List<GetPopupResults.Popup> = listOf()
    var fetched: Boolean = false
}
