package com.wafflestudio.snutt2.core.data.di

import com.wafflestudio.snutt2.core.data.repository.NotificationRepository
import com.wafflestudio.snutt2.core.data.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule{
    @Binds
    internal abstract fun bindsTopicRepository(
        notificationRepository: NotificationRepositoryImpl,
    ): NotificationRepository
}