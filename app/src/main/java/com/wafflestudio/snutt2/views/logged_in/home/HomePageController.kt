package com.wafflestudio.snutt2.views.logged_in.home

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.wafflestudio.snutt2.R

class HomePageController {
    private val _homePageState = mutableStateOf(HomeItem.Timetable)

    @Stable
    val homePageState: State<HomeItem> = _homePageState

    fun update(updater: (HomeItem) -> HomeItem) {
        _homePageState.value = updater(_homePageState.value)
    }

    fun update(page: HomeItem) {
        _homePageState.value = page
    }
}

enum class HomeItem(@DrawableRes val icon: Int) {
    Timetable(R.drawable.ic_timetable),
    Search(R.drawable.ic_search),
    Review(R.drawable.ic_review),
    Settings(R.drawable.ic_setting)
}

