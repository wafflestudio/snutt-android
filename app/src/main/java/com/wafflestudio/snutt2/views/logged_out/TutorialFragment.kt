package com.wafflestudio.snutt2.views.logged_out

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentTutorialBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TutorialFragment : BaseFragment() {

    private lateinit var binding: FragmentTutorialBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.loginButton.throttledClicks()
            .bindUi(this) {
                routeLogin()
            }

        binding.signUpButton.throttledClicks()
            .bindUi(this) {
                routeSignUp()
            }
        binding.container.adapter = TutorialStateAdapter()

        binding.indicator.setViewPager2(binding.container)
    }

    private fun routeSignUp() {
        findNavController().navigate(
            R.id.action_tutorialFragment_to_signUpFragment
        )
    }

    private fun routeLogin() {
        findNavController().navigate(
            R.id.action_tutorialFragment_to_loginFragment
        )
    }

    inner class TutorialStateAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return TutorialPageFragment(position)
        }

    }
}
