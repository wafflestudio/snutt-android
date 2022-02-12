package com.wafflestudio.snutt2.lib.android

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePagerController @Inject constructor() {

    private val _homePageStream = BehaviorSubject.create<HomePage>()
    val homePageState: Observable<HomePage> = _homePageStream.hide()

    fun updateHomePage(page: HomePage) {
        _homePageStream.onNext(page)
    }
}

sealed class HomePage {

    object Timetable : HomePage()

    object Search : HomePage()

    data class Review(val landingUrl: String) : HomePage()

    object Setting : HomePage()
}
