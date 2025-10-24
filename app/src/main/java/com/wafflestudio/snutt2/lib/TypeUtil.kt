package com.wafflestudio.snutt2.lib

inline fun <reified T> (Any?).ifType(action: (T) -> Unit) {
    (this as? T)?.let(action)
}
