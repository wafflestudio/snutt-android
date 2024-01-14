package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PostThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
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
        return _customThemes.value.find { it.id == themeId } ?: CustomTheme.Default
    }

    override fun getTheme(code: Int): BuiltInTheme {
        return _builtInThemes.value.find { it.code == code } ?: BuiltInTheme.SNUTT
    }

    override suspend fun createTheme(name: String, colors: List<ColorDto>) {
        val newTheme = api._postTheme(PostThemeParams(name = name, colors = colors)).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
    }

    override suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>) {
        val newTheme = api._patchTheme(
            themeId = themeId,
            patchThemeParams = PatchThemeParams(name = name, colors = colors),
        ).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply {
            set(indexOfFirst { it.id == newTheme.id }, newTheme)
        }
    }

    override suspend fun copyTheme(themeId: String) {
        val newTheme = api._postCopyTheme(themeId = themeId).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
    }

    override suspend fun deleteTheme(themeId: String) {
        api._deleteTheme(themeId = themeId)
        _customThemes.value = _customThemes.value.toMutableList().apply { removeIf { it.id == themeId } }
    }

    override suspend fun setCustomThemeDefault(themeId: String) {
        val newTheme = api._postCustomThemeDefault(themeId = themeId).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.map {
            it.copy(isDefault = it.id == newTheme.id)
        }
        _builtInThemes.value = _builtInThemes.value.map {
            it.copy(isDefault = false)
        }
    }

    override suspend fun setBuiltInThemeDefault(theme: Int) {
        val newTheme = api._postBuiltInThemeDefault(basicThemeTypeValue = theme).toTableTheme() as BuiltInTheme
        _builtInThemes.value = _builtInThemes.value.map {
            it.copy(isDefault = it.code == newTheme.code)
        }
        _customThemes.value = _customThemes.value.map {
            it.copy(isDefault = false)
        }
    }

    override suspend fun unsetCustomThemeDefault(themeId: String) {
        val newTheme = api._deleteCustomThemeDefault(themeId = themeId).toTableTheme() as? BuiltInTheme
        if (newTheme != null) {
            _builtInThemes.value = _builtInThemes.value.map {
                it.copy(isDefault = it.code == newTheme.code)
            }
        }
        _customThemes.value = _customThemes.value.map {
            it.copy(isDefault = false)
        }
    }
}
