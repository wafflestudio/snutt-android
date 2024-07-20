package com.wafflestudio.snutt2.core.network.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.core.network.SNUTTNetworkDataSource
import com.wafflestudio.snutt2.core.network.retrofit.RetrofitSNUTTNetwork
import com.wafflestudio.snutt2.core.network.util.MoshiSerializer
import com.wafflestudio.snutt2.core.network.util.Serializer
import com.wafflestudio.snutt2.core.qualifiers.CoreNetwork
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SerializerModule {

    @Binds
    @CoreNetwork
    abstract fun bindSerializer(moshiSerializer: MoshiSerializer): Serializer

    @Binds
    abstract fun bindNetwork(impl: RetrofitSNUTTNetwork): SNUTTNetworkDataSource

    companion object {
        @Provides
        @CoreNetwork
        @Singleton
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }
    }
}
