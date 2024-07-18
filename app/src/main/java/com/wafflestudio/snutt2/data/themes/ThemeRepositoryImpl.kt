package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.core.database.model.Table
import com.wafflestudio.snutt2.core.database.preference.SNUTTStorageTemp
import com.wafflestudio.snutt2.core.database.util.map
import com.wafflestudio.snutt2.core.database.util.unwrap
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.qualifiers.CoreDatabase
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.network.dto.core.toExternalModel
import com.wafflestudio.snutt2.lib.network.dto.core.toNetworkModel
import com.wafflestudio.snutt2.lib.toOptional
import com.wafflestudio.snutt2.model.BuiltInTheme
import com.wafflestudio.snutt2.model.CustomTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Singleton
import com.wafflestudio.snutt2.core.network.model.PatchThemeParams as PatchThemeParamsNetwork
import com.wafflestudio.snutt2.core.network.model.PostThemeParams as PostThemeParamsNetwork

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    @CoreNetwork private val api: SNUTTNetworkDataSource,
    @CoreDatabase private val storage: SNUTTStorageTemp,
    externalScope: CoroutineScope,
) : ThemeRepository {

    private val _customThemes = MutableStateFlow<List<CustomTheme>>(emptyList())
    override val customThemes: StateFlow<List<CustomTheme>> = _customThemes

    private val _builtInThemes = MutableStateFlow<List<BuiltInTheme>>(emptyList())
    override val builtInThemes: StateFlow<List<BuiltInTheme>> = _builtInThemes

    override val currentTableTheme = combine(
        storage.lastViewedTable.asStateFlow()
            .unwrap(externalScope).map(externalScope) { it: Table? -> it?.toExternalModel().toOptional() }, // TODO : 변환 함수 사용, revisit 필요
        _customThemes,
    ) { table, _ ->
        table.value?.themeId?.let { themeId ->
            getTheme(themeId)
        } ?: table.value?.theme?.let {
            BuiltInTheme.fromCode(it)
        } ?: BuiltInTheme.SNUTT
    }.stateIn(externalScope, SharingStarted.Eagerly, BuiltInTheme.SNUTT)

    override suspend fun fetchThemes() {
        api._getThemes().let { themes ->
            _customThemes.value = themes.filter { it.isCustom }.map { it.toExternalModel().toTableTheme() as CustomTheme }
            _builtInThemes.value = (0..5).map { code ->
                BuiltInTheme.fromCode(code)
            }
        }
    }

    override fun getTheme(themeId: String): CustomTheme {
        return _customThemes.value.find { it.id == themeId } ?: CustomTheme.Default
    }

    override suspend fun createTheme(name: String, colors: List<ColorDto>): CustomTheme {
        val newTheme = api._postTheme(PostThemeParamsNetwork(name = name, colors = colors.map { it.toNetworkModel() }))
            .toExternalModel().toTableTheme() as CustomTheme // TODO : 여기 코드가 좀 보기 안 좋은 느낌이 있음
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
        return newTheme
    }

    override suspend fun updateTheme(themeId: String, name: String, colors: List<ColorDto>): CustomTheme {
        val newTheme = api._patchTheme(
            themeId = themeId,
            patchThemeParams = PatchThemeParamsNetwork(name = name, colors = colors.map { it.toNetworkModel() }),
        ).toExternalModel().toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply {
            set(indexOfFirst { it.id == newTheme.id }, newTheme)
        }
        return newTheme
    }

    override suspend fun copyTheme(themeId: String) {
        val newTheme = api._postCopyTheme(themeId = themeId).toExternalModel().toTableTheme() as CustomTheme
        _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
    }

    override suspend fun deleteTheme(themeId: String) {
        api._deleteTheme(themeId = themeId)
        _customThemes.value = _customThemes.value.toMutableList().apply { removeIf { it.id == themeId } }
    }
}
