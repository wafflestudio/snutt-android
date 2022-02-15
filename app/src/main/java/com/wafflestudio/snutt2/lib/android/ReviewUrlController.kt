package com.wafflestudio.snutt2.lib.android

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewUrlController @Inject constructor() {

    private val _urlEvent: MutableStateFlow<String?> = MutableStateFlow(null)
    val urlEvent: StateFlow<String?> = _urlEvent

    // TODO: 단 한번 emit 된 옵저버만 불리는 형태로 변경 필요
    fun flushAfterObserve() {
        _urlEvent.value = null
    }

    fun update(url: String) {
        _urlEvent.value = url
    }
}
