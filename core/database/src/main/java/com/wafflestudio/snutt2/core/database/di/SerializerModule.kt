package com.wafflestudio.snutt2.core.database.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.core.database.preference.storage.MoshiSerializer
import com.wafflestudio.snutt2.core.database.preference.storage.Serializer
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// TODO: moshi는 네트워크에서도 쓸테니 별도 모듈로 옮기기?
@Module
@InstallIn(SingletonComponent::class)
abstract class SerializerModule {

    @Binds
    abstract fun bindSerializer(moshiSerializer: MoshiSerializer): Serializer

    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }
}
