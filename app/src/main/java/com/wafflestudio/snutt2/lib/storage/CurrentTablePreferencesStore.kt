package com.wafflestudio.snutt2.lib.storage

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.lib.network.dto.core.TableDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

private const val CURRENT_TABLE_PREFERENCES_FILE_NAME = "current_table_prefs.pb"

val Context.currentTablePreferencesStore: DataStore<CurrentTablePreferences> by dataStore(
    fileName = CURRENT_TABLE_PREFERENCES_FILE_NAME,
    serializer = CurrentTablePreferencesSerializer,
)

@JsonClass(generateAdapter = true)
data class CurrentTablePreferences(
    val data: TableDto?
)

@Suppress("BlockingMethodInNonBlockingContext")
object CurrentTablePreferencesSerializer : Serializer<CurrentTablePreferences> {
    lateinit var moshi: Moshi

    override val defaultValue: CurrentTablePreferences = CurrentTablePreferences(null)

    override suspend fun readFrom(input: InputStream): CurrentTablePreferences {
        return try {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                moshi.adapter(CurrentTablePreferences::class.java)
                    .fromJson(input.source().buffer())
                    ?: throw JsonDataException("data not exists")
            }
        } catch (exception: JsonDataException) {
            throw CorruptionException("Cannot read json.", exception)
        } catch (exception: IOException) {
            // FIXME: 여기서 json encoding exception 때문에 store 에 있는 table 을 불러오지 못한다.
            return defaultValue
        }
    }

    override suspend fun writeTo(t: CurrentTablePreferences, output: OutputStream) {
        @Suppress("BlockingMethodInNonBlockingContext")
        withContext(Dispatchers.IO) {
            moshi.adapter(CurrentTablePreferences::class.java)
                .toJson(output.sink().buffer(), t)
        }
    }
}
