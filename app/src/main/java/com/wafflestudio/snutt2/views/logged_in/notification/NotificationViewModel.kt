package com.wafflestudio.snutt2.views.logged_in.notification

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.rxjava3.observable
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.GetNotificationResults
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
        private val notificationRepository: NotificationRepository,
        private val apiOnError: ApiOnError
) : ViewModel() {
//    val notificationList : DataProvider<GetNotificationResults> = notificationRepository.notificationList

//    fun loadNotification() {
//        // paging 적용 전, 임시로 limit 값, offset 값 설정 ( explicit 은 뭔지 아직 모름)
//        notificationRepository.getNotification(10, 0, 10)
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribeBy(onError = apiOnError)
//    }

//    val notifications : Observable<PagingData<NotificationDto>> =
//        notificationRepository.getNotification(10, 0, 10).observable

    val notifications : Observable<PagingData<String>> =
        notificationRepository.getNotification(10, 0, 10).observable

}
