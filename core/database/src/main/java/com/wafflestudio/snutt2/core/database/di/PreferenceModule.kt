package com.wafflestudio.snutt2.core.database.di

import android.content.Context
import com.wafflestudio.snutt2.core.database.preference.cache.PrefCache
import com.wafflestudio.snutt2.core.database.preference.cache.PrefCacheImpl
import com.wafflestudio.snutt2.core.database.preference.context.PrefContext
import com.wafflestudio.snutt2.core.database.preference.storage.PrefStorage
import com.wafflestudio.snutt2.core.database.preference.storage.PrefStorageImpl
import com.wafflestudio.snutt2.core.database.preference.storage.Serializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class PreferenceModule {
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
