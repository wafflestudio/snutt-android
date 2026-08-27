package com.wafflestudio.snutt2.data.applanguage

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import com.wafflestudio.snutt2.domain.model.AppLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLanguageDataSource @Inject constructor(
    @param:ApplicationContext private val context: Context,
) : ComponentCallbacks {

    private val _appLanguage = MutableStateFlow(context.resources.configuration.toAppLanguage())
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    init {
        context.registerComponentCallbacks(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        _appLanguage.value = newConfig.toAppLanguage()
    }

    @Suppress("OVERRIDE_DEPRECATION")
    override fun onLowMemory() = Unit

    private fun Configuration.toAppLanguage(): AppLanguage = if (locales[0].language == Locale.KOREAN.language) {
        AppLanguage.KOREAN
    } else {
        AppLanguage.ENGLISH
    }
}
