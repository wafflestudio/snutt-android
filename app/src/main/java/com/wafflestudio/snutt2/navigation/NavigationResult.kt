package com.wafflestudio.snutt2.navigation

import android.os.Parcelable
import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

inline fun <reified T : Parcelable> SavedStateHandle.observeResult(key: String): Flow<T> =
    getStateFlow<T?>(key, null)
        .filterNotNull()
        .onEach { remove<T>(key) }
