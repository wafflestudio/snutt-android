package com.wafflestudio.snutt2.data.model

abstract class TableThemeT(
    open val name: String,
)

data class CustomThemeT(
    val id: String,
    override val name: String,
    val colors: List<ColorDtoT>,
) : TableThemeT(name)

data class BuiltInThemeT(
    val code: Int,
    override val name: String,
) : TableThemeT(name)
