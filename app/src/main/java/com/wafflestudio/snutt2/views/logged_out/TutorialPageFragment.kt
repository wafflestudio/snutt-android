package com.wafflestudio.snutt2.views.logged_out

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.databinding.FragmentTutorialPageBinding

class TutorialPageFragment(val pageNum: Int) : Fragment() {

    private lateinit var binding: FragmentTutorialPageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTutorialPageBinding.inflate(inflater, container, false)
        binding.apply {
            introTitle.setImageResource(
                when (pageNum) {
                    0 -> R.drawable.imgintrotitle1
                    1 -> R.drawable.imgintrotitle2
                    2 -> R.drawable.imgintrotitle3
                    else -> throw IllegalStateException()
                }
            )
            introDetail.setImageResource(
                when (pageNum) {
                    0 -> R.drawable.imgintro1
                    1 -> R.drawable.imgintro2
                    2 -> R.drawable.imgintro3
                    else -> throw IllegalStateException()
                }
            )
        }

        return binding.root
    }
}
