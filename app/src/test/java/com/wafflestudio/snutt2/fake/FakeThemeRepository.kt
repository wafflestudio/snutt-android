package com.wafflestudio.snutt2.fake

import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.domainmodel.BuiltInTheme
import com.wafflestudio.snutt2.domainmodel.CustomTheme
import com.wafflestudio.snutt2.domainmodel.ThemeColor
import com.wafflestudio.snutt2.lib.network.Result
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
    override fun getTheme(themeId: String): CustomTheme {
        return getThemeResult ?: error("getThemeResult not set for themeId=$themeId")
    }

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

    // --- 미사용 메서드 ---
    override suspend fun createTheme(name: String, colors: List<ThemeColor>): Result<CustomTheme> = TODO("Not used in this test")
    override suspend fun updateTheme(themeId: String, name: String, colors: List<ThemeColor>): Result<CustomTheme> = TODO("Not used in this test")
}
