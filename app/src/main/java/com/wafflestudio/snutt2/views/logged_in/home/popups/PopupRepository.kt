package com.wafflestudio.snutt2.views.logged_in.home.popups

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PopupList
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class PopupRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi
) {

    fun getPopup(): Single<PopupList> {
        return snuttRestApi.getPopup()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }
}
