package com.wafflestudio.snutt2.layouts.modalBottomSheetLayout

import androidx.compose.runtime.Immutable

@Immutable
@kotlin.jvm.JvmInline
internal value class Strings private constructor(@Suppress("unused") private val value: Int) {
    companion object {
        val NavigationMenu = Strings(0)
        val CloseDrawer = Strings(1)
        val CloseSheet = Strings(2)
        val DefaultErrorMessage = Strings(3)
        val ExposedDropdownMenu = Strings(4)
        val SliderRangeStart = Strings(5)
        val SliderRangeEnd = Strings(6)
    }
}
