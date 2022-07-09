package com.wafflestudio.snutt2.di

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wafflestudio.snutt2.data.TimetableColorThemeAdapter
import com.wafflestudio.snutt2.views.logged_in.home.popups.PopupState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(TimetableColorThemeAdapter())
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun providePopupState(): PopupState {
        return PopupState()
    }
}
