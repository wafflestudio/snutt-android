package com.wafflestudio.snutt2.di

import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OAuthModule {

    @Provides
    @Singleton
    fun provideFacebookCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }

    @Provides
    @Singleton
    fun provideLoginManager(): LoginManager {
        return LoginManager.getInstance()
    }
}
