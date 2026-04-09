package com.wafflestudio.snutt2.data.themes

import com.wafflestudio.snutt2.data.mapper.toTableTheme
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.ThemeColor
import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.network.dto.ColorDto
import com.wafflestudio.snutt2.network.api.SNUTTRestApi
import com.wafflestudio.snutt2.network.dto.PatchThemeParams
import com.wafflestudio.snutt2.network.dto.PostThemeParams
import com.wafflestudio.snutt2.network.error.toDomainError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeRepositoryImpl @Inject constructor(
    private val api: SNUTTRestApi,
) : ThemeRepository {

    private val _customThemes = MutableStateFlow<List<CustomTheme>>(emptyList())
    override val customThemes: StateFlow<List<CustomTheme>> = _customThemes

    private val _builtInThemes = MutableStateFlow<List<BuiltInTheme>>(emptyList())
    override val builtInThemes: StateFlow<List<BuiltInTheme>> = _builtInThemes

    override fun getTheme(themeId: String): CustomTheme {
        return _customThemes.value.find { it.id == themeId } ?: CustomTheme.Default
    }

    override suspend fun fetchThemes(): Result<Unit> {
        return try {
            api._getThemes().let { themes ->
                _customThemes.value =
                    themes.filter { it.isCustom == true }.map { it.toTableTheme() as CustomTheme }
                _builtInThemes.value = (0..5).map { code ->
                    BuiltInTheme.fromCode(code)
                }
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun createTheme(name: String, colors: List<ThemeColor>): Result<CustomTheme> {
        return try {
            val newTheme =
                api._postTheme(PostThemeParams(name = name, colors = colors.map { ColorDto(it.foreground, it.background) }))
                    .toTableTheme() as CustomTheme
            _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
            Result.Success(newTheme)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun updateTheme(
        themeId: String,
        name: String,
        colors: List<ThemeColor>,
    ): Result<CustomTheme> {
        return try {
            val newTheme = api._patchTheme(
                themeId = themeId,
                patchThemeParams = PatchThemeParams(
                    name = name,
                    colors = colors.map { ColorDto(it.foreground, it.background) },
                ),
            ).toTableTheme() as CustomTheme
            _customThemes.value = _customThemes.value.toMutableList().apply {
                set(indexOfFirst { it.id == newTheme.id }, newTheme)
            }
            Result.Success(newTheme)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun copyTheme(themeId: String): Result<Unit> {
        return try {
            val newTheme = api._postCopyTheme(themeId = themeId).toTableTheme() as CustomTheme
            _customThemes.value = _customThemes.value.toMutableList().apply { add(0, newTheme) }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }

    override suspend fun deleteTheme(themeId: String): Result<Unit> {
        return try {
            api._deleteTheme(themeId = themeId)
            _customThemes.value =
                _customThemes.value.toMutableList().apply { removeIf { it.id == themeId } }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Fail(e.toDomainError())
        }
    }
}
