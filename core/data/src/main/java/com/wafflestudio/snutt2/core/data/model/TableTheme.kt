package com.wafflestudio.snutt2.core.data.model

abstract class TableThemeT(
    open val name: String,
)

data class CustomThemeT(
    val id: String,
    override val name: String,
    val colors: List<com.wafflestudio.snutt2.core.data.model.ColorDtoT>,
) : com.wafflestudio.snutt2.core.data.model.TableThemeT(name)

data class BuiltInThemeT(
    val code: Int,
    override val name: String,
) : com.wafflestudio.snutt2.core.data.model.TableThemeT(name)
