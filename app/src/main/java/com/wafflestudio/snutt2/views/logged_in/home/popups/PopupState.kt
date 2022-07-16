package com.wafflestudio.snutt2.views.logged_in.home.popups

class PopupState {

    private var isPopupShownOnce = false

    fun refreshPopupState() {
        isPopupShownOnce = false
    }

    fun getAndUpdatePopupState(): Boolean {
        return if (isPopupShownOnce) true
        else {
            isPopupShownOnce = true
            false
        }
    }
}
