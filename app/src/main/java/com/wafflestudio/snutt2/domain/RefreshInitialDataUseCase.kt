package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.current_table.CurrentTableRepository
import com.wafflestudio.snutt2.data.semester_status.SemesterStatusRepository
import com.wafflestudio.snutt2.data.tables.TableRepository
import com.wafflestudio.snutt2.data.themes.ThemeRepository
import com.wafflestudio.snutt2.data.user.UserRepository
import com.wafflestudio.snutt2.lib.network.onFailure
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class RefreshInitialDataUseCase @Inject constructor(
    private val currentTableRepository: CurrentTableRepository,
    private val tableRepository: TableRepository,
    private val userRepository: UserRepository,
    private val themeRepository: ThemeRepository,
    private val semesterStatusRepository: SemesterStatusRepository,
) {
    suspend operator fun invoke() {
        coroutineScope {
            awaitAll(
                async {
                    currentTableRepository.currentTable.value?.let {
                        tableRepository.fetchTableById(it.summary.id)
                            .onFailure { tableRepository.fetchDefaultTable() }
                    } ?: tableRepository.fetchDefaultTable()
                },
                async { userRepository.fetchUserInfo() },
                async { themeRepository.fetchThemes() },
                async { semesterStatusRepository.fetchSemesterStatus() },
            )
        }
    }
}
