package com.wafflestudio.snutt2.storage.model

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.network.dto.NicknameDto
import com.wafflestudio.snutt2.network.dto.UserDto
import org.junit.Test
import kotlin.test.assertEquals

/**
 * SNUTTStorage 가 [UserDto] 대신 [UserLocalEntity] 를 저장하도록 바뀌었으므로,
 * 기존 사용자 기기에 저장된 JSON 이 [UserLocalEntity] 로도 동일하게 역직렬화되어야 한다.
 * 앱 본체와 동일하게 [KotlinJsonAdapterFactory] 기반 reflection 직렬화를 사용한다.
 */
class UserLocalEntitySerializationTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    private val entityAdapter = moshi.adapter(UserLocalEntity::class.java)
    private val dtoAdapter = moshi.adapter(UserDto::class.java)

    @Test
    fun `legacy UserDto JSON deserializes into equivalent UserLocalEntity`() {
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

    @Test
    fun `UserLocalEntity serializes to byte-equivalent JSON as UserDto`() {
        val cases = listOf(
            UserDto(
                isAdmin = false,
                regDate = "2023-01-01",
                notificationCheckedAt = "2024-05-01",
                email = "a@b.com",
                localId = "alice",
                fbName = "Alice",
                nickname = NicknameDto(nickname = "앨리스", tag = "1234"),
            ) to UserLocalEntity(
                isAdmin = false,
                regDate = "2023-01-01",
                notificationCheckedAt = "2024-05-01",
                email = "a@b.com",
                localId = "alice",
                fbName = "Alice",
                nickname = NicknameLocalEntity(nickname = "앨리스", tag = "1234"),
            ),
            UserDto() to UserLocalEntity(),
            UserDto(isAdmin = true, email = "x@y.z", nickname = NicknameDto("a", "b")) to
                UserLocalEntity(isAdmin = true, email = "x@y.z", nickname = NicknameLocalEntity("a", "b")),
        )

        cases.forEach { (dto, entity) ->
            assertEquals(dtoAdapter.toJson(dto), entityAdapter.toJson(entity))
        }
    }

    @Test
    fun `round-trip serialization preserves UserLocalEntity`() {
        val originals = listOf(
            UserLocalEntity(
                isAdmin = true,
                regDate = "2023-01-01",
                notificationCheckedAt = null,
                email = "test@snutt.kr",
                localId = "test",
                fbName = null,
                nickname = NicknameLocalEntity("닉네임", "9999"),
            ),
            UserLocalEntity(),
        )

        originals.forEach { original ->
            val parsed = entityAdapter.fromJson(entityAdapter.toJson(original))
            assertEquals(original, parsed)
        }
    }
}
