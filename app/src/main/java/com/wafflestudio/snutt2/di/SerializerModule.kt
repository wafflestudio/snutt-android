package com.wafflestudio.snutt2.di

import com.wafflestudio.snutt2.lib.data.serializer.MoshiSerializer
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SerializerModule {

    @Binds
    abstract fun bindSerializer(moshiSerializer: MoshiSerializer): Serializer
}
