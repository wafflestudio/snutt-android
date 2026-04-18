package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domain.model.BuiltInTheme
import com.wafflestudio.snutt2.domain.model.CustomTheme
import com.wafflestudio.snutt2.domain.model.ThemeColor
import kotlinx.coroutines.flow.MutableStateFlow

class FakeThemeRepository : ThemeRepository {

    // --- StateFlow ---
    override val customThemes = MutableStateFlow<List<CustomTheme>>(emptyList())
    override val builtInThemes = MutableStateFlow<List<BuiltInTheme>>(emptyList())

    // --- 테스트 제어용 필드 ---
    var getThemeResult: CustomTheme? = null

    var fetchThemesResult: Result<Unit> = Result.Success(Unit)
    var fetchThemesCalled = false
        private set

    var copyThemeResult: Result<Unit> = Result.Success(Unit)
    var copyThemeCalledWith: String? = null
        private set

    var deleteThemeResult: Result<Unit> = Result.Success(Unit)
    var deleteThemeCalledWith: String? = null
        private set

    // --- 인터페이스 구현 ---
    override fun getTheme(themeId: String): CustomTheme = getThemeResult ?: error("getThemeResult not set for themeId=$themeId")

    override suspend fun fetchThemes(): Result<Unit> {
        fetchThemesCalled = true
        return fetchThemesResult
    }

    override suspend fun copyTheme(themeId: String): Result<Unit> {
        copyThemeCalledWith = themeId
        return copyThemeResult
    }

    override suspend fun deleteTheme(themeId: String): Result<Unit> {
        deleteThemeCalledWith = themeId
        return deleteThemeResult
    }

    var createThemeResult: Result<CustomTheme> = Result.Fail(
        com.wafflestudio.snutt2.domain.Unknown(displayTitle = "", displayMessage = ""),
    )
    var createThemeCalledWith: Pair<String, List<ThemeColor>>? = null
        private set

    var updateThemeResult: Result<CustomTheme> = Result.Fail(
        com.wafflestudio.snutt2.domain.Unknown(displayTitle = "", displayMessage = ""),
    )
    var updateThemeCalledWith: Triple<String, String, List<ThemeColor>>? = null
        private set

    override suspend fun createTheme(name: String, colors: List<ThemeColor>): Result<CustomTheme> {
        createThemeCalledWith = name to colors
        return createThemeResult
    }

    override suspend fun updateTheme(themeId: String, name: String, colors: List<ThemeColor>): Result<CustomTheme> {
        updateThemeCalledWith = Triple(themeId, name, colors)
        return updateThemeResult
    }
}
