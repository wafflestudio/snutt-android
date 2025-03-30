package com.wafflestudio.snutt2.views.logged_in.home.settings

import com.wafflestudio.snutt2.lib.network.dto.GetSocialProvidersResults

data class SocialLinkUiState(
    val facebook: SocialProviders,
    val kakao: SocialProviders,
    val google: SocialProviders,
) {
    enum class SocialProviders {
        LINKED, UNLINKED
    }

    companion object {
        val Default = SocialLinkUiState(
            facebook = SocialProviders.UNLINKED,
            kakao = SocialProviders.UNLINKED,
            google = SocialProviders.UNLINKED,
        )
    }
}

fun GetSocialProvidersResults.socialLinkUiState(): SocialLinkUiState = SocialLinkUiState(
    facebook = if (facebook) SocialLinkUiState.SocialProviders.LINKED else SocialLinkUiState.SocialProviders.UNLINKED,
    kakao = if (kakao) SocialLinkUiState.SocialProviders.LINKED else SocialLinkUiState.SocialProviders.UNLINKED,
    google = if (google) SocialLinkUiState.SocialProviders.LINKED else SocialLinkUiState.SocialProviders.UNLINKED,
)

fun SocialLinkUiState.SocialProviders.isLinked() = (this == SocialLinkUiState.SocialProviders.LINKED)
