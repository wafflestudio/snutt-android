package com.wafflestudio.snutt2.views.logged_in.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.ui.SnuttTheme
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {

    private val vm: NotificationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                SnuttTheme {
                    Column {
                        TopBar(
                            backButtonClick = { findNavController().popBackStack() }
                        )
                        NotificationList(notifications = vm.notifications)
                    }
                }
            }
        }
    }
}
