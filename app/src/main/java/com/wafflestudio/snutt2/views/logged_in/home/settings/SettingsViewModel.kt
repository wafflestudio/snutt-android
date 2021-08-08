package com.wafflestudio.snutt2.views.logged_in.home.settings

import androidx.lifecycle.ViewModel
import com.wafflestudio.snutt2.data.SettingsRepository
import com.wafflestudio.snutt2.lib.data.DataProvider
import com.wafflestudio.snutt2.model.TableTrimParam
import dagger.hilt.android.lifecycle.HiltViewModel
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
