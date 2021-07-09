package com.wafflestudio.snutt2.di

import android.content.Context
import android.content.SharedPreferences
import com.facebook.CallbackManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.lib.preferences.serializer.MoshiSerializer
import com.wafflestudio.snutt2.lib.preferences.serializer.Serializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @InstallIn(SingletonComponent::class)
    @Module
    abstract class SerializerModule {
        @Binds
        abstract fun bindSerializer(moshiSerializer: MoshiSerializer): Serializer
    }
}
