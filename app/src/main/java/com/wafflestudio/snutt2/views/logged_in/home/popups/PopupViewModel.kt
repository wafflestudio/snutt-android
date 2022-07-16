package com.wafflestudio.snutt2.views.logged_in.home.popups

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.dto.GetPopupResults
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(
    private val popupRepository: PopupRepository,
    private val storage: SNUTTStorage
) : ViewModel() {

    fun fetchPopup(): Maybe<GetPopupResults.Popup> {
        return popupRepository.getPopup()
            .observeOn(AndroidSchedulers.mainThread())
            .map { it.popups }
            .flatMapMaybe {
                val filtered = it.filter { popup ->
                    val expireMillis: Long? = storage.shownPopupIdsAndTimestamp.get()[popup.key]
                    val currentMillis = System.currentTimeMillis()
                    (expireMillis == null || currentMillis >= expireMillis)
                }
                if (filtered.isEmpty()) {
                    Maybe.empty()
                } else {
                    Maybe.just(filtered.first())
                }
            }
    }

    fun invalidateShownPopUp(popup: GetPopupResults.Popup) {
        val expiredDay: Long = popup.popupHideDays?.let { hideDays ->
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(hideDays.toLong())
        } ?: INFINITE_LONG_MILLIS

        storage.shownPopupIdsAndTimestamp.update(
            storage.shownPopupIdsAndTimestamp.get()
                .toMutableMap()
                .also {
                    it[popup.key] = expiredDay
                }
        )
    }

    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}
