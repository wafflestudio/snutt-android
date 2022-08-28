package com.wafflestudio.snutt2.lib.storage

import android.content.Context
import android.util.Log
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import okio.source
import java.io.EOFException
import java.io.InputStream
import java.io.OutputStream

private const val TABLE_MAP_PREFERENCES_FILE_NAME = "table_map_prefs.aaatxt"

val Context.tableMapPreferencesStore: DataStore<TableMapPreferences> by dataStore(
    fileName = TABLE_MAP_PREFERENCES_FILE_NAME,
    serializer = TableMapPreferencesSerializer,
)

@JsonClass(generateAdapter = true)
data class TableMapPreferences(
    val map: Map<String, SimpleTableDto>
)

object TableMapPreferencesSerializer : Serializer<TableMapPreferences> {
    lateinit var moshi: Moshi

    override val defaultValue: TableMapPreferences = TableMapPreferences(mapOf())

    override suspend fun readFrom(input: InputStream): TableMapPreferences {
        return try {
            @Suppress("BlockingMethodInNonBlockingContext")
            withContext(Dispatchers.IO) {
                moshi.adapter(TableMapPreferences::class.java)
                    .fromJson(input.source().buffer())
                    ?: throw JsonDataException("data not exists")
            }
        } catch (exception: JsonDataException) {
            throw CorruptionException("Cannot read json.", exception)
        }
    }

    override suspend fun writeTo(t: TableMapPreferences, output: OutputStream) {
        @Suppress("BlockingMethodInNonBlockingContext")
        withContext(Dispatchers.IO) {
            val asdf = moshi.adapter(TableMapPreferences::class.java)
                .toJson(t)
            output.write(asdf.toByteArray())
        }
    }
}
