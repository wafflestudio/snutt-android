package com.wafflestudio.snutt2.data.lecturesearch

import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.junit.Test

class CreditTagParsingTest {

    @Test
    fun `한글과 영문 학점 태그에서 학점 수를 추출한다`() {
        val fixtures = mapOf(
            "1학점" to 1L,
            "3학점" to 3L,
            "3 credits" to 3L,
            "3 Credits" to 3L,
            "  6 credits" to 6L,
        )

        fixtures.forEach { (tag, expectedCredit) ->
            assertEquals(expectedCredit, tag.toCreditNumber())
        }
    }

    @Test
    fun `숫자가 없는 학점 태그는 IllegalArgumentException을 던진다`() {
        val exception = assertFailsWith<IllegalArgumentException> {
            "credits".toCreditNumber()
        }

        assertEquals("Invalid credit tag: credits", exception.message)
    }
}
