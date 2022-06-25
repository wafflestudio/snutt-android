package com.wafflestudio.snutt2.views.logged_in.home

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.PopupDto
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class PopupViewModel @Inject constructor(
    private val prefStorage: PrefStorage,
    private val popupRepository: PopupRepository,
    private val apiOnError: ApiOnError
) : ViewModel() {

    fun fetchPopup(show: (day: Int?, dto: PopupDto) -> Unit) {
        popupRepository.getPopup()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = apiOnError,
                onSuccess = {
                    val expireDate: Int? = prefStorage.getValue("popup", it.key, Int::class.java)

                    // 처음 보는 팝업 or 다시 볼 때가 된 팝업
                    if (expireDate == null || getCurrDay() >= expireDate) {
                        show.invoke(it.days, it)
                    }
                }
            )
    }
}
