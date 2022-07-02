package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.dto.PopupDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Maybe
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(
    private val popupRepository: PopupRepository,
    private val storage: SNUTTStorage
) : ViewModel() {

    fun fetchPopup(): Maybe<PopupDto> {
        return popupRepository.getPopup()
            .observeOn(AndroidSchedulers.mainThread())
            .filter {
                val expireMillis: Long? = storage.popUpAsdfMap.get()[it.key]
                val currentMillis = System.currentTimeMillis()
                (expireMillis == null || currentMillis >= expireMillis)
            }
    }

    fun invalidateShownPopUp(popupDto: PopupDto) {
        val expiredDay: Long = popupDto.popUpHideDays?.let { hideDays ->
            System.currentTimeMillis() + TimeUnit.DAYS.toMillis(hideDays.toLong())
        } ?: INFINITE_LONG_MILLIS

        storage.popUpAsdfMap.update(
            storage.popUpAsdfMap.get()
                .toMutableMap()
                .also {
                    it[popupDto.key] = expiredDay
                }
        )
    }

    companion object {
        const val INFINITE_LONG_MILLIS = Long.MAX_VALUE
    }
}

