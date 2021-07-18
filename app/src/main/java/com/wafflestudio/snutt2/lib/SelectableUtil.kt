package com.wafflestudio.snutt2.lib

fun <T : Any> T.toSelectable(selected: Boolean = false): SelectableLegacy<T> {
    return SelectableLegacy.ofSelected(this, selected = selected)
}

data class SelectableLegacy<T : Any>(
    val item: T,
    val isSelected: Boolean
) {

    companion object {
        fun <T : Any> ofSelected(item: T, selected: Boolean): SelectableLegacy<T> =
            SelectableLegacy(item, selected)
    }
}

