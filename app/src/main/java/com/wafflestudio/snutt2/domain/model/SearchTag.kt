package com.wafflestudio.snutt2.domain.model

import com.wafflestudio.snutt2.domainmodel.TagType

sealed interface SearchTag {
    val type: TagType
    fun toItemKey(): String = toString()

    data class Regular(override val type: TagType, val name: String) : SearchTag

    data object TimeEmpty : SearchTag {
        override val type = TagType.TIME
    }

    data object TimeSelect : SearchTag {
        override val type = TagType.TIME
    }

    data object EtcEng : SearchTag {
        override val type = TagType.ETC
    }

    data object EtcMilitary : SearchTag {
        override val type = TagType.ETC
    }

    companion object {
        val timeTags = listOf(TimeEmpty, TimeSelect)
        val etcTags = listOf(EtcEng, EtcMilitary)
    }
}
