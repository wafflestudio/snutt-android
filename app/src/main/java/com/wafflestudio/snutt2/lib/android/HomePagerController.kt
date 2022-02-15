package com.wafflestudio.snutt2.lib.android

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomePagerController @Inject constructor() {

    private val _homePageStream = MutableStateFlow(HomePage.Timetable)
    val homePageState: StateFlow<HomePage> = _homePageStream

    fun update(updater: (HomePage) -> HomePage) {
        _homePageStream.update(updater)
    }

    fun update(page: HomePage) {
        _homePageStream.value = page
    }
}

enum class HomePage(val pageNum: Int) {
    Timetable(0),
    Search(1),
    Review(2),
    Setting(3),
}
