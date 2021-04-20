package com.wafflestudio.snutt2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.SNUTTBaseFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by makesource on 2017. 6. 23..
 */
@AndroidEntryPoint
class IntroFragment : SNUTTBaseFragment() {

    private var title: ImageView? = null
    private var detail: ImageView? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_intro, container, false)
        val section = requireArguments().getInt(ARG_SECTION_NUMBER)
        title = rootView.findViewById<View>(R.id.intro_title) as ImageView
        detail = rootView.findViewById<View>(R.id.intro_detail) as ImageView
        when (section) {
            0 -> {
                title!!.setImageResource(R.drawable.imgintrotitle1)
                detail!!.setImageResource(R.drawable.imgintro1)
            }
            1 -> {
                title!!.setImageResource(R.drawable.imgintrotitle2)
                detail!!.setImageResource(R.drawable.imgintro2)
            }
            2 -> {
                title!!.setImageResource(R.drawable.imgintrotitle3)
                detail!!.setImageResource(R.drawable.imgintro3)
            }
            else -> {
            }
        }
        return rootView
    }

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"
        fun newInstance(sectionNumber: Int): IntroFragment {
            val fragment = IntroFragment()
            val args = Bundle()
            args.putInt(ARG_SECTION_NUMBER, sectionNumber)
            fragment.arguments = args
            return fragment
        }
    }
}
