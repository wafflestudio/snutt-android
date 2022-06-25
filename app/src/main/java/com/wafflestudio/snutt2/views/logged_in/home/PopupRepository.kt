package com.wafflestudio.snutt2.views.logged_in.home

import android.os.Build
import com.wafflestudio.snutt2.BuildConfig
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PopupDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class PopupRepository @Inject constructor(
    private val snuttRestApi: SNUTTRestApi
) {

    fun getPopup(): Single<PopupDto> {
//        return Single.just(PopupDto("key6", "aaa", 5))

        return snuttRestApi.getPopup(
            osVersion = Build.VERSION.SDK_INT,
            osType = "android",
            appVersion = BuildConfig.VERSION_CODE.toString()
        )
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
    }
}
