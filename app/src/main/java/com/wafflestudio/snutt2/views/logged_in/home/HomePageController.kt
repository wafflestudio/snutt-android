package com.wafflestudio.snutt2.views.logged_in.home

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class HomePageController(
    initialPage: HomeItem = HomeItem.Timetable,
) {
    private val _homePageState = mutableStateOf(initialPage)

    @Stable
    val homePageState: State<HomeItem> = _homePageState

    fun update(updater: (HomeItem) -> HomeItem) {
        _homePageState.value = updater(_homePageState.value)
    }

    fun update(page: HomeItem) {
        _homePageState.value = page
    }
}

sealed class HomeItem {

    object Timetable : HomeItem()
    object Search : HomeItem()
    object Review : HomeItem()
    object Friends : HomeItem()
    object Settings : HomeItem()
}

// enum class HomeItem(@DrawableRes val icon: Int) {
//    Timetable(R.drawable.ic_timetable),
//    Search(R.drawable.ic_search),
//    Review(R.drawable.ic_review),
//    Settings(R.drawable.ic_setting)
// }
//
