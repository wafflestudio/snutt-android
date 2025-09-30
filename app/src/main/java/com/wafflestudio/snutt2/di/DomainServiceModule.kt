package com.wafflestudio.snutt2.di

import com.wafflestudio.snutt2.data.themes.ThemeServiceImpl
import com.wafflestudio.snutt2.domain.ThemeService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DomainServiceModule {

    @Binds
    abstract fun bindsThemeService(impl: ThemeServiceImpl): ThemeService
}