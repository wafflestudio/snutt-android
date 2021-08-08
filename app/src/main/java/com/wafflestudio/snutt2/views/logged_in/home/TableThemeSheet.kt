package com.wafflestudio.snutt2.views.logged_in.home

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.wafflestudio.snutt2.R
import com.wafflestudio.snutt2.data.TimetableColorTheme
import com.wafflestudio.snutt2.databinding.DialogSelectThemeBinding
import com.wafflestudio.snutt2.handler.ApiOnError
import com.wafflestudio.snutt2.lib.network.dto.core.SimpleTableDto
import com.wafflestudio.snutt2.lib.rx.filterEmpty
import com.wafflestudio.snutt2.lib.rx.throttledClicks
import com.wafflestudio.snutt2.views.logged_in.home.timetable.SelectedTimetableViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import javax.inject.Inject

@AndroidEntryPoint
class TableThemeSheet(
    private val tableDto: SimpleTableDto,
) : BottomSheetDialogFragment() {

    private lateinit var binding: DialogSelectThemeBinding

    @Inject
    lateinit var apiOnError: ApiOnError

    private val vm: SelectedTimetableViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSelectThemeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        // FIXME: selected preview 의 scope 를 제한하는 보다 좋은 방법을 찾자
        vm.setSelectedPreviewTheme(null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vm.setSelectedPreviewTheme(vm.lastViewedTable.get().value?.theme)
        binding.confirm.throttledClicks()
            .flatMapCompletable {
                vm.updateTheme(tableDto.id, vm.selectedPreviewTheme.get().value!!)
                    .doOnComplete {
                        dismiss()
                    }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onError = apiOnError
            )

        vm.selectedPreviewTheme.asObservable()
            .filterEmpty()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy {
                mapOf(
                    TimetableColorTheme.SNUTT to binding.snuttText,
                    TimetableColorTheme.MODERN to binding.modernText,
                    TimetableColorTheme.AUTUMN to binding.autumnText,
                    TimetableColorTheme.CHERRY to binding.pinkText,
                    TimetableColorTheme.ICE to binding.iceText,
                    TimetableColorTheme.GRASS to binding.grassText
                ).forEach { (theme, view) ->
                    view.background =
                        if (theme == it) requireContext().getDrawable(R.drawable.background_label)
                        else null
                }
            }

        mapOf(
            binding.snuttButton to TimetableColorTheme.SNUTT,
            binding.modernButton to TimetableColorTheme.MODERN,
            binding.autumnButton to TimetableColorTheme.AUTUMN,
            binding.pinkButton to TimetableColorTheme.CHERRY,
            binding.iceButton to TimetableColorTheme.ICE,
            binding.grassButton to TimetableColorTheme.GRASS
        ).forEach { (view, theme) ->
            view.setOnClickListener {
                vm.setSelectedPreviewTheme(theme)
            }
        }
    }
}
