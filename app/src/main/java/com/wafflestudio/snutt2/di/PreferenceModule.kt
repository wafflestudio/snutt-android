package com.wafflestudio.snutt2.di

import android.content.Context
import com.wafflestudio.snutt2.core.qualifiers.App
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

