package com.wafflestudio.snutt2.lib.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.datastore.migrations.SharedPreferencesMigration
import androidx.datastore.migrations.SharedPreferencesView
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.data.SNUTTStorage
import com.wafflestudio.snutt2.lib.network.dto.core.UserDto
import com.wafflestudio.snutt2.model.TableTrimParam
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.InputStream
import java.io.OutputStream

private const val USER_PREFERENCES_FILE_NAME = "user_prefs.pb"

val Context.userPreferencesStore: DataStore<UserPreferences> by dataStore(
    fileName = USER_PREFERENCES_FILE_NAME,
    serializer = UserPreferencesSerializer,
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context,
                SNUTTStorage.DOMAIN_SCOPE_LOGIN
            ) { sharedPrefs: SharedPreferencesView, currentData: UserPreferences ->
                @Suppress("BlockingMethodInNonBlockingContext")
                withContext(Dispatchers.IO) {
                    if (currentData.accessToken.isEmpty()) return@withContext null
                    val userId =
                        sharedPrefs.getString("pref_key_user_id", null) ?: return@withContext null
                    val accessToken =
                        sharedPrefs.getString("pref_key_x_access_token", null)
                            ?: return@withContext null
                    val userInfo = sharedPrefs.getString("pref_user", null)?.let {
                        UserPreferencesSerializer.moshi.adapter(UserDto::class.java).fromJson(it)
                    } ?: return@withContext null
                    return@withContext UserPreferences(
                        userId = userId,
                        accessToken = accessToken,
                        data = userInfo
                    )
                } ?: currentData
            }
        )
    }
)

@JsonClass(generateAdapter = true)
data class UserPreferences(
    val userId: String = "",
    val accessToken: String = "",
    val data: UserDto? = null,
    val tableTrimParam: TableTrimParam = TableTrimParam.Default
)

// TODO: DataStore 에서 데이터 업데이트 시 read flow 에 serialize 과정 없이 업데이트가 전달되는지 확인하기
// TODO: fromJson, toJson 이 IO Dispatcher 안에서 inappropriate thread warning 뜨는 이유 확인하기
object UserPreferencesSerializer : Serializer<UserPreferences> {
    lateinit var moshi: Moshi

    override val defaultValue: UserPreferences = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences {
        return try {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                moshi.adapter(UserPreferences::class.java)
                    .fromJson(input.source().buffer()) ?: throw JsonDataException("data not exists")
            }
        } catch (exception: JsonDataException) {
            throw CorruptionException("Cannot read json.", exception)
        }
    }

    override suspend fun writeTo(t: UserPreferences, output: OutputStream) {
        @Suppress("BlockingMethodInNonBlockingContext")
        withContext(Dispatchers.IO) {
            // FIXME: sink & buffer 로 하면 write 가 안됌 그래서 일단 임시로 처리함
            val asdf = moshi.adapter(UserPreferences::class.java).toJson(t)
            output.write(asdf.toByteArray())
        }
    }
}
