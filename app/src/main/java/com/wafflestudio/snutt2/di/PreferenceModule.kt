package com.wafflestudio.snutt2.di

import android.content.Context
import android.content.SharedPreferences
import com.wafflestudio.snutt2.lib.preferences.serializer.Serializer
import com.wafflestudio.snutt2.lib.preferences.storage.PrefStorage
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
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(
            context.packageName + "_preferences",
            Context.MODE_PRIVATE
        )
    }

    @Provides
    @Singleton
    fun providePrefStorage(
        sharedPreferences: SharedPreferences,
        serializer: Serializer
    ): PrefStorage {
        return PrefStorage(sharedPreferences, serializer)
    }
}
