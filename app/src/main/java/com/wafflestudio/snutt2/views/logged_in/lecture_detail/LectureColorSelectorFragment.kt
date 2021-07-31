package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.databinding.FragmentLectureColorSelectorBinding
import com.wafflestudio.snutt2.databinding.ItemLectureColorBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LectureColorSelectorFragment : BaseFragment() {

    private lateinit var binding: FragmentLectureColorSelectorBinding

    private val vm: LectureDetailViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLectureColorSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (vm.colorTheme ?: TimetableColorTheme.SNUTT).let { theme ->
            listOf<ItemLectureColorBinding>(
                binding.colorOne,
                binding.colorTwo,
                binding.colorThree,
                binding.colorFour,
                binding.colorFive,
                binding.colorSix,
                binding.colorSeven,
                binding.colorEight,
            )
                .forEachIndexed { index, item ->
                    item.bgColor.setBackgroundColor(
                        theme.getColorByIndex(
                            requireContext(),
                            (index + 1).toLong()
                        )
                    )
                    item.name.text = theme.name + (index + 1)
                    item.root.setOnClickListener {
                        selectAndPop(index + 1)
                    }
                }
        }

//        layout.setOnClickListener {
//            ColorPickerDialog.Builder(view.context)
//                .setTitle("색상 선택")
//                .setPositiveButton(R.string.common_ok, object : ColorEnvelopeListener {
//                    override fun onColorSelected(envelope: ColorEnvelope?, fromUser: Boolean) {
//                        Timber.d(envelope?.color.toString())
//                    }
//                })
//                .show()
//        }

    }

    private fun selectAndPop(index: Int) {
        vm.setSelectedColor(index, null)
        findNavController().popBackStack()
    }


}
