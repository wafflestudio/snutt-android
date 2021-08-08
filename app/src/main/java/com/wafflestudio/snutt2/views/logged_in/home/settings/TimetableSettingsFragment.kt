package com.wafflestudio.snutt2.views.logged_in.home.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jakewharton.rxbinding4.widget.checkedChanges
import com.wafflestudio.snutt2.databinding.FragmentTimetableSettingsBinding
import com.wafflestudio.snutt2.lib.base.BaseFragment
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.lib.toDayString

class TimetableSettingsFragment : BaseFragment() {

    private lateinit var binding: FragmentTimetableSettingsBinding

    private val vm: SettingsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTimetableSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.trimParam.get().let { trim ->
            binding.switchAuto.isChecked = trim.forceFitLectures
            binding.dayOfWeekRange.dayRangeBar.apply {
                setRangePinsByIndices(
                    trim.dayOfWeekFrom,
                    trim.dayOfWeekTo
                )
                setFormatter { value ->
                    value.toInt().toDayString(requireContext())
                }
            }

            binding.timeRange.classRangeBar.setRangePinsByIndices(
                trim.hourFrom - 8,
                trim.hourTo - 8
            )
        }

        binding.switchAuto.checkedChanges()
            .bindUi(this) {
                vm.setAutoTrim(it)
            }

        vm.trimParam.asObservable().map { it.forceFitLectures }
            .bindUi(this) {
                binding.dayOfWeekRange.root.visibility = if (it.not()) View.VISIBLE else View.GONE
                binding.timeRange.root.visibility = if (it.not()) View.VISIBLE else View.GONE
            }

        binding.backButton.throttledClicks()
            .bindUi(this) {
                findNavController().popBackStack()
            }

        binding.dayOfWeekRange.dayRangeBar.setOnRangeBarChangeListener { _, leftPinIndex, rightPinIndex, _, _ ->
            vm.setDayOfWeekRange(leftPinIndex, rightPinIndex)
        }

        binding.timeRange.classRangeBar.setOnRangeBarChangeListener { _, leftPinIndex, rightPinIndex, _, _ ->
            vm.setHourRange(leftPinIndex + 8, rightPinIndex + 8)
        }
    }
}
