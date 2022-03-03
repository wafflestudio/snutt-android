package com.wafflestudio.snutt2.views.logged_in.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.databinding.FragmentNotificationBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.NotificationDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationFragment : BaseFragment() {

    private lateinit var binding : FragmentNotificationBinding
    private val vm : NotificationViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        val adapter = NotificationAdapter()

        binding.contents.adapter = adapter
        vm.loadNotification()

        vm.notificationList.asObservable()
            .bindUi(this) { list ->
                adapter.submitList(
                    // map<T, R>(...) 은 (T)를 R로 바꿔서 List<R>로 반환.
                    // list.map(dd)
                    // 그냥 {...} 안에 써서 하면 편함.
                    list.map<NotificationDto, NotificationAdapter.Data> {NotificationAdapter.Data.Notification(it)}
                )
            }
    }
    val dd : (NotificationDto) -> NotificationAdapter.Data  = {
        NotificationAdapter.Data.Notification(it)
    }
}
