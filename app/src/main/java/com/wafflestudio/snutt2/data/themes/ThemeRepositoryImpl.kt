package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PostThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : ThemeRepository {

    private val _customThemes = MutableStateFlow<List<CustomTheme>>(emptyList())
    override val customThemes: StateFlow<List<CustomTheme>>
        get() = _customThemes

    private val _builtInThemes = MutableStateFlow<List<BuiltInTheme>>(emptyList())
    override val builtInThemes: StateFlow<List<BuiltInTheme>>
        get() = _builtInThemes

    override suspend fun fetchThemes() {
        api._getThemes().let { themes ->
            _customThemes.value = themes.filter { it.isCustom }.map { it.toTableTheme() as CustomTheme }
            _builtInThemes.value = (0..5).map { code ->
                BuiltInTheme.fromCode(code).copy(
                    isDefault = themes.find { it.theme == code }?.isDefault ?: false,
                )
            }
        }
    }

    override fun getTheme(themeId: String): CustomTheme {
        return _customThemes.value.find { it.id == themeId } ?: CustomTheme.New
    }

    override fun getTheme(code: Int): BuiltInTheme {
        return _builtInThemes.value.find { it.code == code } ?: BuiltInTheme.SNUTT
    }

    override suspend fun createTheme(name: String, colors: List<ColorDto>): TableTheme {
        return api._postTheme(PostThemeParams(name = name, colors = colors)).toTableTheme()
    }

    override suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>): TableTheme {
        return api._patchTheme(
            themeId = themeId,
            patchThemeParams = PatchThemeParams(name = name, colors = colors),
        ).toTableTheme()
    }

    override suspend fun copyTheme(themeId: String): TableTheme {
        return api._postCopyTheme(themeId = themeId).toTableTheme()
    }

    override suspend fun deleteTheme(themeId: String) {
        api._deleteTheme(themeId = themeId)
    }

    override suspend fun setCustomThemeDefault(themeId: String): TableTheme {
        return api._postCustomThemeDefault(themeId = themeId).toTableTheme()
    }

    override suspend fun setBuiltInThemeDefault(theme: Int): TableTheme {
        return api._postBuiltInThemeDefault(basicThemeTypeValue = theme).toTableTheme()
    }

    override suspend fun unsetCustomThemeDefault(themeId: String): TableTheme {
        return api._deleteCustomThemeDefault(themeId = themeId).toTableTheme()
    }
}
