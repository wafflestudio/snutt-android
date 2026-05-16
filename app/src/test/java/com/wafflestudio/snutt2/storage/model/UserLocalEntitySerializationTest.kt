package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test
import kotlin.test.assertEquals

/**
 * 기존 사용자 기기 SharedPreference 에 저장된 JSON 이 [UserLocalEntity] 로 정확히 역직렬화되는지 검증.
 * 이 fixture 의 JSON 모양 = SharedPreference 직렬화 ABI.
 */
class UserLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(UserLocalEntity::class.java)

    @Test
    fun `legacy JSON deserializes into equivalent UserLocalEntity`() {
        val fixtures = listOf(
            """{"isAdmin":false,"regDate":"2023-01-01","notificationCheckedAt":"2024-05-01","email":"a@b.com","localId":"alice","fbName":"Alice","nickname":{"nickname":"앨리스","tag":"1234"}}""" to
                UserLocalEntity(
                    isAdmin = false,
                    regDate = "2023-01-01",
                    notificationCheckedAt = "2024-05-01",
                    email = "a@b.com",
                    localId = "alice",
                    fbName = "Alice",
                    nickname = NicknameLocalEntity(nickname = "앨리스", tag = "1234"),
                ),
            """{"isAdmin":true,"email":"admin@snutt.kr","localId":"admin","nickname":{"nickname":"관리자","tag":"0001"}}""" to
                UserLocalEntity(
                    isAdmin = true,
                    regDate = null,
                    notificationCheckedAt = null,
                    email = "admin@snutt.kr",
                    localId = "admin",
                    fbName = null,
                    nickname = NicknameLocalEntity(nickname = "관리자", tag = "0001"),
                ),
            """{}""" to UserLocalEntity(),
        )

        fixtures.forEach { (json, expected) ->
            assertEquals(expected, entityAdapter.fromJson(json))
        }
    }
}
