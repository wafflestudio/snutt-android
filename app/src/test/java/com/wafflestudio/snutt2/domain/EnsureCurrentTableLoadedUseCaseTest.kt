package com.wafflestudio.snutt2.domain

import com.wafflestudio.snutt2.data.Result
import com.wafflestudio.snutt2.fake.FakeTableRepository
import com.wafflestudio.snutt2.fixture.TestFixtures.courseBook2025_1
import com.wafflestudio.snutt2.fixture.TestFixtures.table
import com.wafflestudio.snutt2.fixture.TestFixtures.tableSummary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EnsureCurrentTableLoadedUseCaseTest {

    private val tableRepository = FakeTableRepository()
    private val useCase = EnsureCurrentTableLoadedUseCase(tableRepository)

    @Test
    fun `마지막으로 본 시간표가 있으면 해당 시간표를 조회한다`() = runTest {
        val currentTable = table(summary = tableSummary(courseBook = courseBook2025_1))
        tableRepository.currentTable.value = currentTable

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(currentTable.summary.id, tableRepository.fetchAndSelectTableCalledWith)
        assertEquals(0, tableRepository.fetchAndSelectDefaultTableCallCount)
    }

    @Test
    fun `마지막으로 본 시간표 조회에 실패하면 기본 시간표를 조회한다`() = runTest {
        tableRepository.currentTable.value = table(summary = tableSummary(courseBook = courseBook2025_1))
        tableRepository.fetchAndSelectTableResult = Result.Fail(Unknown("", ""))

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(1, tableRepository.fetchAndSelectDefaultTableCallCount)
    }

    @Test
    fun `첫 기본 시간표 조회가 실패하면 한 번 재시도한다`() = runTest {
        tableRepository.fetchAndSelectDefaultTableResults += Result.Fail(Unknown("", ""))
        tableRepository.fetchAndSelectDefaultTableResults += Result.Success(Unit)

        val result = useCase()

        assertTrue(result is Result.Success)
        assertEquals(2, tableRepository.fetchAndSelectDefaultTableCallCount)
    }

    @Test
    fun `기본 시간표 조회가 두 번 실패하면 실패를 반환한다`() = runTest {
        tableRepository.fetchAndSelectDefaultTableResult = Result.Fail(Unknown("", ""))

        val result = useCase()

        assertTrue(result is Result.Fail)
        assertEquals(2, tableRepository.fetchAndSelectDefaultTableCallCount)
    }
}
