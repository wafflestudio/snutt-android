package com.wafflestudio.snutt2.core.network.model

abstract class TableTheme(
    open val name: String,
)

data class CustomTheme(
    val id: String,
    override val name: String,
    val colors: List<ColorDto>,
) : TableTheme(name)

data class BuiltInTheme(
    val code: Int,
    override val name: String,
) : TableTheme(name)
