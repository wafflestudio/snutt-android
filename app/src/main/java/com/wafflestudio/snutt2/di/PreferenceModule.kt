package com.wafflestudio.snutt2.di

import android.content.Context
import com.wafflestudio.snutt2.lib.data.serializer.Serializer
import com.wafflestudio.snutt2.lib.preferences.cache.PrefCache
import com.wafflestudio.snutt2.lib.preferences.cache.PrefCacheImpl
import com.wafflestudio.snutt2.lib.preferences.context.PrefContext
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorageImpl
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
    fun providePrefContext(prefStorage: PrefStorage, prefCache: PrefCache): PrefContext {
        return PrefContext(prefStorage, prefCache)
    }

    @Provides
    @Singleton
    fun providePrefStorage(
        @ApplicationContext context: Context,
        serializer: Serializer,
    ): PrefStorage {
        return PrefStorageImpl(context, serializer)
    }

    @Provides
    @Singleton
    fun providePrefCache(): PrefCache {
        return PrefCacheImpl(64)
    }
}
