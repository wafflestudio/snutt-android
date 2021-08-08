package com.wafflestudio.snutt2.views.logged_in.lecture_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.wafflestudio.snutt2.DialogController
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.databinding.FragmentLectureColorSelectorBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.network.dto.core.ColorDto
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@AndroidEntryPoint
class LectureColorSelectorFragment : BaseFragment() {

    private lateinit var binding: FragmentLectureColorSelectorBinding

    private val vm: LectureDetailViewModel by viewModels({ requireParentFragment() })

    @Inject
    lateinit var dialogController: DialogController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLectureColorSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (vm.colorTheme ?: TimetableColorTheme.SNUTT).let { theme ->
            listOf(
                binding.colorOne,
                binding.colorTwo,
                binding.colorThree,
                binding.colorFour,
                binding.colorFive,
                binding.colorSix,
                binding.colorSeven,
                binding.colorEight,
                binding.colorNine
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
                        vm.setSelectedColor(index + 1, null)
                        parentFragmentManager.popBackStack()
                    }
                }
        }

        binding.backButton.throttledClicks()
            .bindUi(this) {
                parentFragmentManager.popBackStack()
            }

        binding.colorCustom.let { item ->
            item.bgColor.setBackgroundColor(
                vm.selectedColor.get().value?.second?.bgColor
                    ?: requireContext().getColor(R.color.white)
            )
            item.fgColor.setBackgroundColor(
                vm.selectedColor.get().value?.second?.fgColor
                    ?: requireContext().getColor(R.color.white)
            )

            item.name.text = "커스텀"

            item.root.throttledClicks()
                .bindUi(this) {
                    dialogController.showColorSelector("글자 색")
                        .flatMap { fgColor ->
                            dialogController.showColorSelector("배경 색").map { Pair(fgColor, it) }
                        }
                        .subscribeBy { (fgColor, bgColor) ->
                            vm.setSelectedColor(0, ColorDto(fgColor, bgColor))
                            parentFragmentManager.popBackStack()
                        }
                }
        }
    }
}
