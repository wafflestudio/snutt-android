package com.wafflestudio.snutt2.di

import android.content.Context
import com.wafflestudio.snutt2.lib.serializer.Serializer
import com.wafflestudio.snutt2.storage.pref.PrefCache
import com.wafflestudio.snutt2.storage.pref.PrefCacheImpl
import com.wafflestudio.snutt2.storage.pref.PrefContext
import com.wafflestudio.snutt2.storage.pref.PrefStorage
import com.wafflestudio.snutt2.storage.pref.PrefStorageImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object PreferenceModule {
    @Provides
    @Singleton
    fun providePrefContext(prefStorage: PrefStorage, prefCache: PrefCache): PrefContext = PrefContext(prefStorage, prefCache)

    @Provides
    @Singleton
    fun providePrefStorage(
        @ApplicationContext context: Context,
        serializer: Serializer,
    ): PrefStorage = PrefStorageImpl(context, serializer)

    @Provides
    @Singleton
    fun providePrefCache(): PrefCache = PrefCacheImpl(64)
}
