package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.semesterstatus.SemesterStatusRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RefreshInitialDataUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val themeRepository: ThemeRepository,
    private val semesterStatusRepository: SemesterStatusRepository,
) {
    suspend operator fun invoke() {
        coroutineScope {
            awaitAll(
                async { userRepository.fetchUserInfo() },
                async { themeRepository.fetchThemes() },
                async { semesterStatusRepository.fetchSemesterStatus() },
            )
        }
    }
}
