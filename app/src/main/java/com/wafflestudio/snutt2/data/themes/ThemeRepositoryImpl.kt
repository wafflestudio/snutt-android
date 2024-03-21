package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.map
import com.wafflestudio.snutt2.lib.network.ApiOnError
import com.wafflestudio.snutt2.lib.network.SNUTTRestApi
import com.wafflestudio.snutt2.lib.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.lib.network.dto.PostThemeParams
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import com.wafflestudio.snutt2.model.TableTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
    private val storage: SNUTTStorage,
    externalScope: CoroutineScope,
    private val apiOnError: ApiOnError
) : ThemeRepository {

    private val _customThemes = MutableStateFlow<List<CustomTheme>>(emptyList())
    override val customThemes: StateFlow<List<CustomTheme>> = _customThemes

    private val _builtInThemes = MutableStateFlow<List<BuiltInTheme>>(emptyList())
    override val builtInThemes: StateFlow<List<BuiltInTheme>> = _builtInThemes

    private val _currentTableTheme = MutableStateFlow<TableTheme>(BuiltInTheme.SNUTT)
    override val currentTableTheme = _currentTableTheme // FIXME: themeRepository에 넣기보다는 currentTableRepository와 themeRepository에 의존하는 UseCase를 만드는 게 낫지 않을까

    init {
        externalScope.launch {
            runCatching {
                fetchThemes()
            }.onFailure(apiOnError)
            storage.lastViewedTable.asStateFlow().map { table ->
                table.value?.themeId?.let {
                    getTheme(it)
                } ?: table.value?.theme?.let {
                    BuiltInTheme.fromCode(it)
                } ?: BuiltInTheme.SNUTT
            }.collect {
                _currentTableTheme.value = it
            }
        }
    }

    override suspend fun fetchThemes() {
        api._getThemes().let { themes ->
            _customThemes.value = themes.filter { it.isCustom }.map { it.toTableTheme() as CustomTheme }
            _builtInThemes.value = (0..5).map { code ->
                BuiltInTheme.fromCode(code)
            }
        }
    }

    override fun getTheme(themeId: String): CustomTheme {
        return _customThemes.value.find { it.id == themeId } ?: CustomTheme.Default
    }

    override suspend fun createTheme(name: String, colors: List<ColorDto>): CustomTheme {
        val newTheme = api._postTheme(PostThemeParams(name = name, colors = colors)).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
        return newTheme
    }

    override suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>): CustomTheme {
        val newTheme = api._patchTheme(
            themeId = themeId,
            patchThemeParams = PatchThemeParams(name = name, colors = colors),
        ).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply {
            set(indexOfFirst { it.id == newTheme.id }, newTheme)
        }
        return newTheme
    }

    override suspend fun copyTheme(themeId: String) {
        val newTheme = api._postCopyTheme(themeId = themeId).toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
    }

    override suspend fun deleteTheme(themeId: String) {
        api._deleteTheme(themeId = themeId)
        _customThemes.value = _customThemes.value.toMutableList().apply { removeIf { it.id == themeId } }
    }
}
