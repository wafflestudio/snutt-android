package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SettingsRepository
import com.wafflestudio.snutt2.data.UserRepository
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

    val trimParam: DataProvider<TableTrimParam> = settingsRepository.tableTrimParam

    fun setHourRange(from: Int, to: Int) {
        settingsRepository.setTableTrim(hourFrom = from, hourTo = to)
    }

    fun setDayOfWeekRange(from: Int, to: Int) {
        settingsRepository.setTableTrim(dayOfWeekFrom = from, dayOfWeekTo = to)
    }

    fun setAutoTrim(enable: Boolean) {
        settingsRepository.setTableTrim(isAuto = enable)
    }
}
