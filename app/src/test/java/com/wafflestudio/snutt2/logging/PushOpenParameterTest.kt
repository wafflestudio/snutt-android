package com.wafflestudio.snutt2.logging

import org.junit.Test
import kotlin.test.assertEquals

class PushOpenParameterTest {

    @Test
    fun `url_scheme 의 query 는 버리고 host 만 path 로 남긴다`() {
        assertEquals(
            PushOpenParameter(pushId = "diary-20261014-1900", pushType = "diary", path = "diary"),
            PushOpenParameter.of(
                urlScheme = "snutt://diary?lectureId=695affb59dfd1a77c7c20778&courseTitle=%EC%BB%B4%ED%93%A8%ED%84%B0",
                pushId = "diary-20261014-1900",
                pushType = "diary",
            ),
        )
    }

    @Test
    fun `외부 url 은 host 와 path 를 남기고 query 와 fragment 를 버린다`() {
        assertEquals(
            PushOpenParameter(pushId = null, pushType = "notice", path = "snutt.wafflestudio.com/notice/12"),
            PushOpenParameter.of(
                urlScheme = "https://snutt.wafflestudio.com/notice/12/?ref=push#top",
                pushId = null,
                pushType = "notice",
            ),
        )
    }

    @Test
    fun `구 서버 푸시처럼 push_id, push_type 이 없거나 비어 있으면 null 로 둔다`() {
        assertEquals(
            PushOpenParameter(pushId = null, pushType = null, path = "vacancy"),
            PushOpenParameter.of(urlScheme = "snutt://vacancy", pushId = "", pushType = null),
        )
    }

    @Test
    fun `url_scheme 이 비어 있으면 path 는 null 이다`() {
        assertEquals(
            PushOpenParameter(pushId = "notice-1", pushType = "notice", path = null),
            PushOpenParameter.of(urlScheme = "", pushId = "notice-1", pushType = "notice"),
        )
    }
}
