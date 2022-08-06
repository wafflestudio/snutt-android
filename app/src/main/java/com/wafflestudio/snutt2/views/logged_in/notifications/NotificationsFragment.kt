package com.wafflestudio.snutt2.views.logged_in.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.wafflestudio.snutt2.databinding.FragmentNotificationsBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment() {
    private val vm: NotificationsViewModel by activityViewModels()
    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var adapter: NotificationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NotificationsAdapter()

        binding.contents.adapter = adapter

//        lifecycleScope.launchWhenResumed {
//            vm.getNotificationsPagingData()
//                .collect {
//                    adapter.submitData(it)
//                }
//        }

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        adapter.addLoadStateListener { loadState ->
            if (loadState.source.refresh is LoadState.NotLoading && loadState.append.endOfPaginationReached && adapter.itemCount < 1) {
                binding.contents.isVisible = false
                binding.placeholder.isVisible = true
                binding.error.isVisible = false
            } else if (loadState.refresh is LoadState.Error) {
                binding.contents.isVisible = false
                binding.placeholder.isVisible = false
                binding.error.isVisible = true
            } else {
                binding.contents.isVisible = true
                binding.placeholder.isVisible = false
                binding.error.isVisible = false
            }
        }
    }
}
