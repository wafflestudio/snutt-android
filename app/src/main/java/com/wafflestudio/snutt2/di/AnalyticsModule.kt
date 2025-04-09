package com.wafflestudio.snutt2.di

import android.content.Context
import com.wafflestudio.snutt2.lib.logging.AnalyticsLogger
import com.wafflestudio.snutt2.lib.logging.FirebaseAnalyticsLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsLogger(
        @ApplicationContext context: Context,
    ): AnalyticsLogger {
        return FirebaseAnalyticsLogger(context)
    }
}
